package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.page.service.impl *
 * @since 1.0
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Override
    public void genHtml(Long id) {
        //查询数据库的商品的数据   生成静态页面

        //1.根据SPU的ID 查询商品的信息（goods  goodsDesc  ）
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

        //2.使用freemarker 创建模板  使用数据集 生成静态页面 (数据集 和模板)
        genHTML("item.ftl",tbGoods,tbGoodsDesc);

    }

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    private void genHTML(String templateName, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        FileWriter writer =null;
        try {
            //1.创建一个configuration对象
            //2.设置字符编码 和 模板加载的目录
            Configuration configuration = configurer.getConfiguration();
            //3.获取模板对象
            Template template = configuration.getTemplate(templateName);
            //4.获取数据集
            Map model = new HashMap();
            model.put("tbGoods",tbGoods);
            model.put("tbGoodsDesc",tbGoodsDesc);

            //查询分类  放入数据集中
            TbItemCat tbItemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            model.put("tbItemCat1",tbItemCat1.getName());
            model.put("tbItemCat2",tbItemCat2.getName());
            model.put("tbItemCat3",tbItemCat3.getName());

            //查询商品SPU的对应的所有的SKU的列表数据
            //select * from tb_item where goods_id=1 and status=1 order by is_default desc

            TbItemExample exmaple = new TbItemExample();
            TbItemExample.Criteria criteria = exmaple.createCriteria();
            criteria.andGoodsIdEqualTo(tbGoods.getId());
            criteria.andStatusEqualTo("1");



            exmaple.setOrderByClause("is_default desc");//order by  is_default desc

            List<TbItem> tbItems = tbItemMapper.selectByExample(exmaple);

            model.put("skuList",tbItems);
            //5.创建一个写流
            writer = new FileWriter(new File("F:\\freemarker\\"+tbGoods.getId()+".html"));
            //6.调用模板对象的process 方法输出到指定的文件中

            template.process(model,writer);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //7.关闭流
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
