package com.itheima.itheimasms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.itheimasms.controller *
 * @since 1.0
 */
@RestController
public class TestController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    @RequestMapping("/send")
    public String send(){
        Map<String,String> map = new HashMap<>();
        map.put("mobile","17373201258");
        map.put("sign_name","黑马三国的包子");
        map.put("template_code","SMS_126865257");
        map.put("param","{\"code\":\"123456\"}");
        jmsTemplate.convertAndSend(destination,map);
        return "ok";
    }
}
