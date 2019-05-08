package com.pinyougou.solr.test;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package PACKAGE_NAME *
 * @since 1.0
 */
@ContextConfiguration("classpath:spring/spring-solr.xml")
@RunWith(SpringRunner.class)
public class TestSolr {

    @Autowired
    private SolrTemplate solrTemplate;

    //添加文档（创建索引）

    @Test
    public void addIndex(){
        //1.注入solrtemplate
        //2.使用保存的方法
        TbItem tbItem = new TbItem();
        tbItem.setId(1001L);
        tbItem.setTitle("测试商品的标题");
        solrTemplate.saveBean(tbItem);//pojo { id,name,title}========>document:{field2:id,field1:name,field3:title}
        //3.commit
        solrTemplate.commit();
    }

    //更新文档
    @Test
    public void updateiNDEX(){
        //先删除  再添加
        //1.注入solrtemplate
        //2.使用保存的方法
        TbItem tbItem = new TbItem();
        tbItem.setId(1001L);
        tbItem.setTitle("测试商品的标题1111");
        solrTemplate.saveBean(tbItem);//pojo { id,name,title}========>document:{field2:id,field1:name,field3:title}
        //3.commit
        solrTemplate.commit();
    }
    @Test
    public void save(){
        //先删除  再添加
        //1.注入solrtemplate
        //2.使用保存的方法

        for (Long i = 1L; i < 100; i++) {
            TbItem tbItem = new TbItem();
            tbItem.setId(100+i);
            tbItem.setTitle("测试商品的标题"+i);
            solrTemplate.saveBean(tbItem);//pojo { id,name,title}========>document:{field2:id,field1:name,field3:title}
            //3.commit
            solrTemplate.commit();
        }

    }





    //删除文档
    @Test
    public void testDeleteById(){
        solrTemplate.deleteById("1001");
        solrTemplate.commit();
    }

    @Test
    public void testDeleteByQuery(){
        Query query = new SimpleQuery("*:*");// 创建一个查询的对象

        solrTemplate.delete(query);// 相当于delete from tb_item where title like ""
        solrTemplate.commit();
    }

    @Test
    public void testDeleteByQueryCondition(){
        Query query = new SimpleQuery("*:*");// 创建一个查询的对象
        Criteria criteria = new Criteria("item_goodsid");//goods_id in ()
        List<Long> list = new ArrayList<>();
        list.add(149187842867968L);
        list.add(149187842867966L);
        criteria.in(list);
        query.addCriteria(criteria);

        solrTemplate.delete(query);// 相当于delete from tb_item where title like ""
        solrTemplate.commit();
    }

    //查询文档
    @Test
    public void testSelectById(){
        TbItem tbItem = solrTemplate.getById("1001", TbItem.class);
        System.out.println(tbItem.getTitle());
    }

    //分页查询

    @Test
    public void testSelectByPage(){
        //1.创建一个查询的条件对象  每页显示的行(rows)  （当前的页码-1）* rows  加入一些查询的条件
        Query query = new SimpleQuery();


        Criteria criteria = new Criteria("item_title");
        criteria.contains("钛灰色");//  item_title:10
        query.addCriteria(criteria);

        query.setOffset(0);// （当前的页码-1）* rows
        query.setRows(5);//每页显示5个


        //2.执行分页查询的方法

        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);

        //3.获取分页的结果
        System.out.println(tbItems.getTotalElements());
        System.out.println(tbItems.getTotalPages());
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }


    }




}
