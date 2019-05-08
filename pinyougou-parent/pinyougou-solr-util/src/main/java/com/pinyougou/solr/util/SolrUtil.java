package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.solr.util *
 * @since 1.0
 */
@Component
public class SolrUtil {
    //逻辑：就是查询数据库的数据导入到索引库中
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importFromDBToIndex(){
            //1.查询数据库的数据
        TbItemExample exmaple = new TbItemExample();
        exmaple.createCriteria().andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(exmaple);
        //循环遍历
        for (TbItem tbItem : tbItems) {
            //取出规格{key:value} string
            String spec = tbItem.getSpec();//{key:value}//{"网络":"移动3G","机身内存":"16G"}
            //转成JSON Map
            Map map = JSON.parseObject(spec, Map.class);
            //map设置到规格的对应的动态域的属性中
            tbItem.setSpecMap(map);
        }





        //2.使用solrtemplate
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }


}
