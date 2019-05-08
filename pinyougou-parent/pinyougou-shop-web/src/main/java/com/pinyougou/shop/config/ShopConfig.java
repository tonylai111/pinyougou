package com.pinyougou.shop.config;

import com.pinyougou.shop.security.UserDetailsServiceImpl;
import org.apache.http.impl.conn.Wire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.config *
 * @since 1.0
 */
@EnableWebSecurity
public class ShopConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

   /* @Bean//作用就相当于：<bean class="com.pinyougou.shop.security.UserDetailsServiceImpl" id="createUserDetailsService">
    public UserDetailsService createUserDetailsService(){
        return new UserDetailsServiceImpl();
    }*/

   @Autowired
   private PasswordEncoder passwordEncoder;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //从数据库中查询用户名和密码
       //auth.inMemoryAuthentication().withUser("zhangsanfeng").password("123456").roles("SELLER");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
       //url的映射配置
        http.authorizeRequests()
                .antMatchers("/*.html","/css/**","/img/**","/js/**","/plugins/**","/seller/add.do").permitAll()
                .antMatchers("/**").hasRole("SELLER")
                .anyRequest().permitAll();
        // 登录配置
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .successHandler(successHandler)

                .failureHandler(failureHandler);

               // .failureUrl("/shoplogin.html?error");
        //csrf的禁用
        http.csrf().disable();
        //设置同源访问frame的策略
        http.headers().frameOptions().sameOrigin();
    }



    @Autowired
    private SavedRequestAwareAuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successHandler(){
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        return successHandler;
    }

    @Bean
    public AuthenticationFailureHandler createMapping (){
//        SimpleUrlAuthenticationFailureHandler fai = new SimpleUrlAuthenticationFailureHandler();
//        fai.setDefaultFailureUrl("/admin/index.html");
        AuthenticationFailureHandler fai= new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                httpServletResponse.setContentType("application/json;charset=utf-8");

                //httpServletResponse.sendRedirect("/jsp?message=");
                PrintWriter writer = httpServletResponse.getWriter();
                writer.println("<html>");

                writer.flush();
                writer.close();
            }
        };

        return fai;
    }
}
