package com.pinyougou.shop.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.security *
 * @since 1.0
 */
public class UserDetailsServiceImpl implements UserDetailsService {


    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //从数据库中查询商家信息
        System.out.println("进过了loadUserByUsername");
        //1.需要先根据商家名查询商家的对象信息
        TbSeller one = sellerService.findOne(username);

        if(one==null) {
            //2.判断是否存在商家信息 ，如果不存在  直接返回null
            return null;
        }
        if(!"1".equals(one.getStatus())){
        //3.判断 商家的状态是否是 已审核 如果 没有审核 直接返回null
            return null;
        }

        //4.获取商家的密码 然后进行匹配（springsecurity的框架自动完成）
        //赋予权限
       /* List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));*/

        return new User(username,one.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
    }
}
