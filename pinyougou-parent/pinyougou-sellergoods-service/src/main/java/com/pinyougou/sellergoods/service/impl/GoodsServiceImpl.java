package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
//@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper tbItemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper tbSellerMapper;

	@Autowired
	private TbBrandMapper brandMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//1.获取SPU的数据
		TbGoods goods1 = goods.getGoods();
		//2.获取SPU的描述的数据
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		//3.获取SKU的List数据

		//4.插入三个表数据(item不管)
		goods1.setAuditStatus("0");//未审核的状态
		goods1.setIsDelete(false);//不删除的状态
		goodsMapper.insert(goods1);



		goodsDesc.setGoodsId(goods1.getId());//需要mapper的主键返回
		goodsDescMapper.insert(goodsDesc);

		//插入 商品的SKU列表  判断 如果是启用规格 和不启用规格都需要判断 一起实现即可

		saveItems(goods, goods1, goodsDesc);
	}

	private void saveItems(Goods goods, TbGoods goods1, TbGoodsDesc goodsDesc) {
		if("1".equals(goods1.getIsEnableSpec())) {

			//TODO
			//先获取SKU的列表
			List<TbItem> itemList = goods.getItemList();

			for (TbItem tbItem : itemList) {

				//设置title  SPU名 + 空格+ 规格名称 +
				String spec = tbItem.getSpec();//{"网络":"移动4G","机身内存":"16G"}
				String title = goods1.getGoodsName();
				Map map = JSON.parseObject(spec, Map.class);
				for (Object key : map.keySet()) {
					String o1 = (String) map.get(key);
					title += " " + o1;
				}
				tbItem.setTitle(title);

				//设置图片从goodsDesc中获取
				//[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
				String itemImages = goodsDesc.getItemImages();//

				List<Map> maps = JSON.parseArray(itemImages, Map.class);

				String url = maps.get(0).get("url").toString();//图片的地址
				tbItem.setImage(url);

				//设置分类
				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
				tbItem.setCategoryid(tbItemCat.getId());
				tbItem.setCategory(tbItemCat.getName());

				//时间
				tbItem.setCreateTime(new Date());
				tbItem.setUpdateTime(new Date());

				//设置SPU的ID
				tbItem.setGoodsId(goods1.getId());

				//设置商家
				TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods1.getSellerId());
				tbItem.setSellerId(tbSeller.getSellerId());
				tbItem.setSeller(tbSeller.getNickName());//店铺名

				//设置品牌明后
				TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods1.getBrandId());
				tbItem.setBrand(tbBrand.getName());
				tbItemMapper.insert(tbItem);
			}
		}else{
			//插入到SKU表 一条记录
			TbItem tbItem = new TbItem();
			tbItem.setTitle(goods1.getGoodsName());
			tbItem.setPrice(goods1.getPrice());
			tbItem.setNum(999);//默认一个
			tbItem.setStatus("1");//正常启用
			tbItem.setIsDefault("1");//默认的

			tbItem.setSpec("{}");


			//设置图片从goodsDesc中获取
			//[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
			String itemImages = goodsDesc.getItemImages();//

			List<Map> maps = JSON.parseArray(itemImages, Map.class);

			String url = maps.get(0).get("url").toString();//图片的地址
			tbItem.setImage(url);

			//设置分类
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
			tbItem.setCategoryid(tbItemCat.getId());
			tbItem.setCategory(tbItemCat.getName());

			//时间
			tbItem.setCreateTime(new Date());
			tbItem.setUpdateTime(new Date());

			//设置SPU的ID
			tbItem.setGoodsId(goods1.getId());

			//设置商家
			TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods1.getSellerId());
			tbItem.setSellerId(tbSeller.getSellerId());
			tbItem.setSeller(tbSeller.getNickName());//店铺名

			//设置品牌明后
			TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods1.getBrandId());
			tbItem.setBrand(tbBrand.getName());
			tbItemMapper.insert(tbItem);
		}
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//1.更新SPU
		TbGoods goods1 = goods.getGoods();
		goods1.setAuditStatus("0");// 修改都需要设置状态为0
		goodsMapper.updateByPrimaryKey(goods1);
		//2.更新描述
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		//3.更新SKU
		List<TbItem> itemList = goods.getItemList();

		//先删除原来的SKU的列表

		//delete from tb_item where goods_id = 1
		TbItemExample exmaple = new TbItemExample();
		exmaple.createCriteria().andGoodsIdEqualTo(goods1.getId());
		tbItemMapper.deleteByExample(exmaple);
		//新增
		saveItems(goods,goods1,goods.getGoodsDesc());

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//1.获取SPU 的数据
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

		//2.获取SPU对应的描述的数据
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		//3.SKU的列表的数据
			//select * from tb_item where goods_id=1
		TbItemExample exmaple = new TbItemExample();
		exmaple.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> tbItems = tbItemMapper.selectByExample(exmaple);
		//4.组合对象返回
		Goods goods =new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);

		goods.setItemList(tbItems);
		
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		/*for(Long id:ids){
			//update set is_deelte =0 where id = 1
			goodsMapper.deleteByPrimaryKey(id);
		}	*/
		//update set is_deelte =0 where id  in (1,2,3)

		TbGoods recrod = new TbGoods();//更新后的数据
		recrod.setIsDelete(true);//已经删除

		TbGoodsExample exmaple = new TbGoodsExample();
		exmaple.createCriteria().andIdIn(Arrays.asList(ids));
		goodsMapper.updateByExampleSelective(recrod,exmaple);

		int i=1/0;

	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		//排除已经删除的数据
		criteria.andIsDeleteEqualTo(false);//select * from tb_goods where is_delete=0 and  and dand

		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void updateStatus(Long[] ids) {

		//update  tb_goods set audit_status=1 where id in (1,2,3)


		TbGoods record = new TbGoods();
		record.setAuditStatus("1");

		TbGoodsExample exmaple = new TbGoodsExample();
		Criteria criteria = exmaple.createCriteria();
		criteria.andIdIn(Arrays.asList(ids));
		//第一个参数为 要更新后的数据

		goodsMapper.updateByExampleSelective(record,exmaple);


    }



	@Override
	public List<TbItem> findItemListByIds(Long[] ids) {

		TbItemExample exmaple = new TbItemExample();
		TbItemExample.Criteria criteria = exmaple.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		criteria.andStatusEqualTo("1");
		return tbItemMapper.selectByExample(exmaple);
	}

}
