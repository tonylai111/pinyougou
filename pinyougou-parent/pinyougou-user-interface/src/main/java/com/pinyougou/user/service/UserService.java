package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.user.service *
 * @since 1.0
 */
public interface UserService {
    void add(TbUser user);


    //生成短信验证码 并发送消息
    void createCode(String phone);


    boolean ischecked(String phone, String code);


}
