package com.lowi.admin.Interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * LoginConfiguration.java
 * ==============================================
 * Copy right 2015-2017 by http://www.51lick.com
 * ----------------------------------------------
 * This is not a free software, without any authorization is not allowed to use and spread.
 * ==============================================
 *
 * @author : gengyy
 * @version : v2.0
 * @desc :
 * @since : 2019/12/18 18:50
 */

@Configuration
public class LoginConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        LoginInterceptor loginInterceptor = new LoginInterceptor();
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(loginInterceptor);
        interceptorRegistration.addPathPatterns("/**");
        interceptorRegistration.excludePathPatterns("/toLogin");
        interceptorRegistration.excludePathPatterns("/login");
        interceptorRegistration.excludePathPatterns("/logout");
        interceptorRegistration.excludePathPatterns("/error");
        interceptorRegistration.excludePathPatterns("/js/**");
        interceptorRegistration.excludePathPatterns("/images/**");
        interceptorRegistration.excludePathPatterns("/css/**");
        interceptorRegistration.excludePathPatterns("/lib/**");
        interceptorRegistration.excludePathPatterns("/layui/**");
        interceptorRegistration.excludePathPatterns("/register");

    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        //addResourceHandler请求路径
//        //addResourceLocations 在项目中的资源路径
//        //setCacheControl 设置静态资源缓存时间
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        super.addResourceHandlers(registry);
//    }
}
