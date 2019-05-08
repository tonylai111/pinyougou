package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.jms.Destination;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.user.service.impl *
 * @since 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Override
    public void add(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(user.getCreated());
        //密码要进行加密 md5加密
        String miwen = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(miwen);

        userMapper.insert(user);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    @Override
    public void createCode(String phone) {
        //2.拼接签名 模板的code
        //3.随机生成一个验证码的值 拼接  6位数

        double random = Math.random();
        double v = random * 1000000;
        long code=(long)v;

        //4.存储验证码 到  redis

        redisTemplate.boundValueOps("yanzhengma_"+phone).set(code+"");

        //key的过期时间   String
        //hash 过期大key

        redisTemplate.boundValueOps("yanzhengma_"+phone).expire(24, TimeUnit.HOURS);


        //5.组成消息内容，调用jsmtemplate的方法 发送消息即可
        Map<String,String> map = new HashMap<>();

        map.put("mobile",phone);
        map.put("sign_name","黑马三国的包子");
        map.put("template_code","SMS_126865257");
        map.put("param","{\"code\":\""+code+"\"}");

        //发送消息需要用到jms:+ activmeq的依赖  + 配置文件：1.连接工厂 2.目的地 3.jmstemplate

        jmsTemplate.convertAndSend(destination,map);



    }

    @Override
    public boolean ischecked(String phone, String code) {
        //从redis中根据key 获取值  对比 OK
       String codefromredis = (String) redisTemplate.boundValueOps("yanzhengma_"+phone).get();
       if(StringUtils.isEmpty(codefromredis)){
           return false;
       }
       if(!codefromredis.equals(code)){
           return false;
       }

        return true;
    }

    public static void main(String[] args) {
        double random = Math.random();
        double v = random * 1000000;
        long code=(long)v;
        System.out.println(code);
    }
}
