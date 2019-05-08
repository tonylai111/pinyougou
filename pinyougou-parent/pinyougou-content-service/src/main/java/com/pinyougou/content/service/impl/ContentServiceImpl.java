package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.common.util.SystemConstants;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentCategoryMapper;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;


	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {


		contentMapper.insert(content);
		//清空缓存
		redisTemplate.boundHashOps(SystemConstants.REDIS_CONTENT_KEY).delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//清空缓存

		//1.获取原来的分类的ID

		//获取数据库中的广告的对象
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
		Long categoryIdold = tbContent.getCategoryId();
		//2.获取修改后的分类的ID
		Long categoryId = content.getCategoryId();

		contentMapper.updateByPrimaryKey(content);

		//清空两个
		if(categoryIdold.longValue()==categoryId.longValue()){
			redisTemplate.boundHashOps(SystemConstants.REDIS_CONTENT_KEY).delete(categoryIdold);
		}else {
			redisTemplate.boundHashOps(SystemConstants.REDIS_CONTENT_KEY).delete(categoryIdold);
			redisTemplate.boundHashOps(SystemConstants.REDIS_CONTENT_KEY).delete(categoryId);
		}
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//清空缓存
		for(Long id:ids){
			contentMapper.deleteByPrimaryKey(id);
			//获取要删除的广告 对应的广告分类的ID
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);

			redisTemplate.boundHashOps(SystemConstants.REDIS_CONTENT_KEY).delete(tbContent.getCategoryId());
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getContent()!=null && content.getContent().length()>0){
				criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Autowired
	private RedisTemplate redisTemplate;

    @Override
    public List<TbContent> getContentList(Long categoryId) {
		//select * from tb_contnet where category_id = 1

		//1.先查询reids中的数据  判断 如果有数据  直接返回
		List<TbContent> contentsRedis = (List<TbContent>) redisTemplate.boundHashOps("REDIS_CONTENT_ContentServiceImpl_").get(categoryId);

		if(contentsRedis!=null && contentsRedis.size()>0){
			System.out.println("有缓存");
			return contentsRedis;
		}

		//2.没有数据 就查询数据库

		TbContentExample exmaple= new TbContentExample();
		exmaple.createCriteria().andCategoryIdEqualTo(categoryId);
		List<TbContent> contents = contentMapper.selectByExample(exmaple);

		//3.数据写入redis

		redisTemplate.boundHashOps("REDIS_CONTENT_ContentServiceImpl_").put(categoryId,contents);
		System.out.println("没有缓存");

		return contents;
	}

}
