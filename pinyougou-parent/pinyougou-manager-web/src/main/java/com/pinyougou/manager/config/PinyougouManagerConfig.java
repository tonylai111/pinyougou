package com.pinyougou.manager.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.manager.config *
 * @since 1.0
 */
@EnableWebSecurity
public class PinyougouManagerConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //路径的映射
        http.authorizeRequests()
                .antMatchers("/login.html","/css/**","/img/**","/js/**","/plugins/**").permitAll()
                .antMatchers("/**").hasRole("ADMIN")
                .anyRequest().authenticated();
        //定义登录页面
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login.html?error");

       //csrf 禁用掉
        http.csrf().disable();
        //设置iframe的同源访问策略

        http.headers().frameOptions().sameOrigin();//disable

        //退出
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

    }
}
