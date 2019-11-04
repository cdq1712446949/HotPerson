package com.chenu.pvcstumansys.config;

import com.chenu.pvcstumansys.compoent.MyMd5PasswordEncoder;
import com.chenu.pvcstumansys.db.bean.User;
import com.chenu.pvcstumansys.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

/**
 * 基本作用：SpringSecurity配置类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    /**
     * 配置 http请求 授权规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 定制请求的授权规则
        http.authorizeRequests()
                .antMatchers("/usr/login").permitAll()                  /* 登录页面任何人都可以访问 */
                .antMatchers("/error").permitAll()                      /* 不拦截错误页面 */
                // 不拦截资源
                .antMatchers("/asserts/**", "/webjars/**").permitAll()
                // 不拦截测试页面 和 自定义错误页面
                .antMatchers("/test**", "/myerror").permitAll()
                /* 登录用户才可进入后台页面 */
                .antMatchers("/**", "/index/**").hasRole("USER");

        // 开启自动配置的登录功能
        http.formLogin().loginPage("/usr/login")
                // 配置登录成功处理者
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        HttpSession session = request.getSession();
                        // 将登录成功的用户放入seession中
                        Object obj = authentication.getPrincipal();
                        if(obj instanceof User){
                            // 说明是普通管理员
                            User user = (User) obj;
                            session.setAttribute("login_usrid", user.getpUid());
                            session.setAttribute("login_usr", user.getUsername());
                            session.setAttribute("login_user_role", user.getRole());
                            session.setAttribute("login_usr_privilege", user.getPrivilege());
                            logger.info("当前登录用户 --> " + user.toString());
                        } else {
                            // 说明是固有的root用户
                            UserDetails userDetails = (UserDetails)obj;
                            session.setAttribute("login_usrid", 0);
                            session.setAttribute("login_usr", userDetails.getUsername());
                            logger.info("当前登录用户 --> " + userDetails.toString());
                        }

                        // 重定向到主页
                        response.sendRedirect("/");
                    }
                }
        );

        // 开启自动配置的注销功能，注销成功后来到首页
        http.logout().logoutSuccessUrl("/");

        // 开启记住我功能
        http.rememberMe().rememberMeParameter("remember");

    }

    /**
     * 定制认证规则
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("root").password("root").roles("ROOT", "USER");   /* 系统固有root用户 */
        // 设置用户从数据库中获取，并使用MD5加密
        auth.userDetailsService(userService).passwordEncoder(new MyMd5PasswordEncoder());
    }

}
