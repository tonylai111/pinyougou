package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.search.service.impl *
 * @since 1.0
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map resultMap = new HashMap();

        //1.主要查询 （高亮查询）
        Map map = searchList(searchMap);
        resultMap.putAll(map);

        //2.分组查询 商品的分类列表
        String keywords = (String) searchMap.get("keywords");
        List<String> categoryList= searchCateoryKeyWords(keywords);

        resultMap.put("categoryList",categoryList);

        //3.获取缓存中的品牌列表和规格列表（默认的情况下获取第一个商品分类的品牌和规格列表）


        //
        String category = (String) searchMap.get("category");
        if(StringUtils.isNotBlank(category)){
            Map specAndBrandMap=searchBrandListAndSpecList(category);
            resultMap.putAll(specAndBrandMap);
        }else{
            if(categoryList!=null && categoryList.size()>0) {
                Map specAndBrandMap = searchBrandListAndSpecList(categoryList.get(0));
                resultMap.putAll(specAndBrandMap);
            }
        }
        return resultMap;
    }

    @Override
    public void updateIndex(List<TbItem> skuList) {
        solrTemplate.saveBeans(skuList);
        solrTemplate.commit();
    }

    @Override
    public void deleteByIds(Long[] ids) {

        Query query = new SimpleQuery("*:*");// 创建一个查询的对象
        Criteria criteria = new Criteria("item_goodsid");//goods_id in ()

        criteria.in(ids);
        query.addCriteria(criteria);

        solrTemplate.delete(query);// 相当于delete from tb_item where title like ""
        solrTemplate.commit();

    }

    //用到redis

    @Autowired
    private RedisTemplate redisTemplate;


    private Map searchBrandListAndSpecList(String categoryName) {
        Map map = new HashMap();
        //1.从缓存中根据分类的名称获取  模板的ID
        Long  typeId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        //2.从缓存中根据模板的ID 获取品牌列表  和规格列表
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);

        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        //3.返回
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }

    //select category from tb_item where title like '%手机%' GROUP BY category
    private List<String> searchCateoryKeyWords(String keywords) {

        keywords = keywords.replace(" ","");

        List<String> list = new ArrayList<>();
        //1.创建查询的条件
        Query query = new SimpleQuery();
        Criteria criteria  = new Criteria("item_keywords");
        criteria.is(keywords);
        query.addCriteria(criteria);//where title like '%手机%'


        //2.设置分组选项
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");// GROUP BY category
        query.setGroupOptions(groupOptions);

        //3.执行分组查询
        //select category from tb_item
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);

        //4.获取分组的结果 （）

        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");

        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());//手机
           // tbItemGroupEntry.getResult();//手机这个分类 对应 所有的商品的列表
        }
        return list;
    }

    private Map searchList(Map searchMap){
        Map resultMap = new HashMap();
        //1.获取关键字的值：手机
        String keywords = (String) searchMap.get("keywords");

         keywords = keywords.replace(" ","");
        //2.高亮查询的查询对象
        HighlightQuery query = new SimpleHighlightQuery();

        Criteria criteria  = new Criteria("item_keywords");
        criteria.is(keywords);
        query.addCriteria(criteria);

        //3.设置高亮选项（1.开启高亮 2.设置高亮显示的域 3.设置前缀 和后缀）
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//设置高亮显示的域

        highlightOptions.setSimplePrefix("<em style=\"color:red\">");
        highlightOptions.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptions);




        //3.1 过滤查询   商品分类的过滤查询

        String category = (String) searchMap.get("category");
        if(StringUtils.isNotBlank(category)) {
            FilterQuery filetquery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_category");
            criteria1.is(category);
            filetquery.addCriteria(criteria1);//item_category:平板电视
            query.addFilterQuery(filetquery);
        }

        //3.2 过滤查询   品牌的过滤查询   item_brand:华为
        String brand = (String) searchMap.get("brand");
        if(StringUtils.isNotBlank(brand)) {
            FilterQuery filetquery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_brand");
            criteria1.is(brand);
            filetquery.addCriteria(criteria1);//item_category:平板电视
            query.addFilterQuery(filetquery);
        }

        //3.3 过滤查询  规格的过滤查询   item_spec_网络:移动4G

        Map<String,String> map = (Map) searchMap.get("spec");

        if(map!=null){
            for (String key : map.keySet()) {
                FilterQuery filetquery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_spec_"+key);
                criteria1.is(map.get(key));
                filetquery.addCriteria(criteria1);//item_category:平板电视
                query.addFilterQuery(filetquery);
            }
        }

        //3.4 过滤价格
        String price = (String) searchMap.get("price");// 0-500
        if(StringUtils.isNotBlank(price)){

            String[] split = price.split("-");

            FilterQuery filetquery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_price");
            //[0 TO 20]
            if(!split[1].equals("*")) {
                criteria1.between(split[0], split[1], true, true);
            }else{
                criteria1.greaterThanEqual(split[0]);
            }
            filetquery.addCriteria(criteria1);//item_price:[0 TO 20]
            query.addFilterQuery(filetquery);
        }


        //3.5 分页

        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");

        if(pageNo==null){
            pageNo=1;
        }
        if(pageSize==null){
            pageSize=40;
        }
        query.setOffset((pageNo-1)*pageSize);//page-1 * rows
        query.setRows(pageSize);//


        //价格的排序   有分 升序  和降序

        String sortField = (String) searchMap.get("sortField");
        String sortType = (String) searchMap.get("sortType");

        if(StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)){
            if(sortType.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }else{
                Sort sort = new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
        }


        //4.获取分页的结果 返回
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取高亮的数据
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();

        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
            TbItem entity = tbItemHighlightEntry.getEntity();
            System.out.println(">>>>>>title:"+entity.getTitle());

            if(tbItemHighlightEntry.getHighlights()!=null
                    && tbItemHighlightEntry.getHighlights().size()>0
                    && tbItemHighlightEntry.getHighlights().get(0)!=null
                    && tbItemHighlightEntry.getHighlights().get(0).getSnipplets()!=null
                    && tbItemHighlightEntry.getHighlights().get(0).getSnipplets().size()>0)
            entity.setTitle(tbItemHighlightEntry.getHighlights().get(0).getSnipplets().get(0));



           /* List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
            for (HighlightEntry.Highlight highlight : highlights) {
                System.out.println("高亮显示的域："+highlight.getField().getName());
                List<String> snipplets = highlight.getSnipplets();
                for (String snipplet : snipplets) {
                    System.out.println("------------------------高亮的东西");
                    System.out.println(snipplet);
                    entity.setTitle(snipplet);
                }
            }*/

        }

        resultMap.put("rows",tbItems.getContent());//当前页的记录
        resultMap.put("totalPages",tbItems.getTotalPages());//
        resultMap.put("total",tbItems.getTotalElements());//

        return resultMap;
    }
}
