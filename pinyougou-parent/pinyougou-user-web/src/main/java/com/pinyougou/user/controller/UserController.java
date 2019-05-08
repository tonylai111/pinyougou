package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.user.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Reference
    private UserService userService;

    /**
     *
     * @param user
     * @param code  页面传递过来的验证码
     * @return
     */
    @RequestMapping("/register")
    public Result add(@RequestBody TbUser user,String code){
        try {
            //先要进行校验 如果校验成功 才能注册成功
            if(!PhoneFormatCheckUtils.isPhoneLegal(user.getPhone())){
                return new Result(false,"手机号不正确");
            }
            //从页面中获取短信验证码  获取手机号  从redis中根据key 查询存储的验证码 和页码传递过来的页码 对比 成功，

            if(!userService.ischecked(user.getPhone(),code)){
                return new Result(false,"校验失败,验证码不正确");
            }

            userService.add(user);

            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    @RequestMapping("/createCode")
    public Result createCode(String phone){
        try {
            //1.验证手机号是否为正确的手机号
            //
            if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
                return new Result(false,"手机号不正确");
            }

            userService.createCode(phone);
            return new Result(true,"请查看你的手机");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"错误");
        }

    }
}
