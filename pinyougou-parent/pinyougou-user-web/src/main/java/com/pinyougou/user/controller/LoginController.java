package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.user.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getInfo")
    public Map getInfo(){
        HashMap hashMap = new HashMap();
        hashMap.put("loginName", SecurityContextHolder.getContext().getAuthentication().getName());
        return  hashMap;
    }
}
