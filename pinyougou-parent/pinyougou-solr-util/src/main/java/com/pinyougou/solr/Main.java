package com.pinyougou.solr;

import com.pinyougou.solr.util.SolrUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.solr *
 * @since 1.0
 */
public class Main {
    public static void main(String[] args) {
        //1.SolrUtil交给spring容器管理
        //2.初始化spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/spring-solr.xml");
        //3.获取solruitl的实例  调用方法 即可
        SolrUtil bean = context.getBean(SolrUtil.class);

        bean.importFromDBToIndex();
    }
}
