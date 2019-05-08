package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.group.Goods;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.jms.Destination;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取登录的商家的ID
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);

			//发送消息
			//删除索引库
			itemSearchService.deleteByIds(ids);



			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		//设置只查询当前登录的用户的SPU的列表
		return goodsService.findPage(goods, page, rows);
	}


	@Reference
	private ItemSearchService itemSearchService;

	@Reference
	private ItemPageService itemPageService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Resource(name="queue_solr_update")
	private Destination destination;


	@Resource(name="topic_genhtml")
	private Destination topic_genhtml;


	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids){
		try {
			goodsService.updateStatus(ids);

			//发送消息

			//更新索引库    调用 搜索的服务中的服务方法(逻辑全部封装到搜索服务中)


			//select * from tb_item where goods_id in (ids)
			List<TbItem> itemListByIds = goodsService.findItemListByIds(ids);

			//调用搜索服务的方法 （1.先获取 SPU的ID 对应的所有的SKU的列表数据，2.更新索引库）

//			itemSearchService.updateIndex(itemListByIds);
			String s = JSON.toJSONString(itemListByIds);

			jmsTemplate.convertAndSend(destination,s);


			//2.调用静态化服务  生成静态页面。
/*
			for (Long id : ids) {
				//根据商品SPU的ID 生成商品的数据
				itemPageService.genHtml(id);
			}*/

			//发送消息

			jmsTemplate.convertAndSend(topic_genhtml,ids);





			return new Result(true,"成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"失败");
		}
	}
	
}
