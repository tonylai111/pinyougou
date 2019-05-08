package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);

		//添加缓存
		//1.查询所有的模板的数据
		List<TbTypeTemplate> all = findAll();
		//2.循环遍历 存储到redis中
		for (TbTypeTemplate tbTypeTemplate : all) {
			String brandIds = tbTypeTemplate.getBrandIds();
			List<Map> maps = JSON.parseArray(brandIds, Map.class);
			//品牌
			redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),maps);
			//规格
			List<Map> specList = findSpecList(tbTypeTemplate.getId());

			redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
		}




		return new PageResult(page.getTotal(), page.getResult());

	}

	/**
	 *
	 * @param id 模板的ID
	 * @return [{"id":27,"text":"网络",options:[{optionName:"移动3G"}]}];
	 */
    @Override
    public List<Map> findSpecList(Long id) {
    	//获取模板对象
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		//获取模板对象中的规格的列表数据（String）[{"id":27,"text":"网络"}]
		String specIds = tbTypeTemplate.getSpecIds();//[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]

		//转换STRING 变成JSON对象List<Map>

		List<Map> maps = JSON.parseArray(specIds, Map.class);

		//循环遍历List<>  获取Map 获取id 的值  根据ID 的值查询规格选项表的记录  再拼接
		for (Map map : maps) {
			//map={"id":27,"text":"网络"}
			Integer id1 = (Integer) map.get("id");//27


			TbSpecificationOptionExample exmaple = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = exmaple.createCriteria();
			criteria.andSpecIdEqualTo(Long.valueOf(id1));
			List<TbSpecificationOption> options = optionMapper.selectByExample(exmaple);//select * from tb_specification_option where spec_id=27

			map.put("options",options);
		}
		return maps;
    }

}
