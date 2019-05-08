package com.pinyougou.content.test;

import com.pinyougou.common.util.SystemConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.content.test *
 * @since 1.0
 */

//   ApplicationContext context = new ClassPathXmlApplicationContext("");
@ContextConfiguration(locations="classpath:spring/applicationContext-redis.xml")
@RunWith(SpringRunner.class)
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //String
    @Test
    public void setValue1(){
        SystemConstants systemConstants = new SystemConstants();
        redisTemplate.boundValueOps("key11").set(""+1);//set(key1,value1)
        Object key111 = redisTemplate.boundValueOps("key111").get();
        System.out.println(key111);
    }

    //hash
    @Test
    public void setValue2(){
        redisTemplate.boundHashOps("bigkey").put("field1","value1");
        redisTemplate.boundHashOps("bigkey").put("field2","value2");

        //获取值
        System.out.println(redisTemplate.boundHashOps("bigkey").get("field2"));
        System.out.println(redisTemplate.boundHashOps("bigkey").get("field1"));

        //所有的值
        List bigkey = redisTemplate.boundHashOps("bigkey").values();
        for (Object o : bigkey) {
            System.out.println("value"+o);
        }
        Set key3= redisTemplate.boundHashOps("bigkey").keys();

        for (Object o : key3) {
            System.out.println("key"+o);
        }


    }
}
