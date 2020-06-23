package com.lowi.admin.Interceptor;

import com.lowi.admin.config.ProfileConfig;
import com.lowi.admin.entity.User;
import com.lowi.admin.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * LoginInterceptor.java
 * ==============================================
 * Copy right 2015-2017 by http://www.51lick.com
 * ----------------------------------------------
 * This is not a free software, without any authorization is not allowed to use and spread.
 * ==============================================
 *
 * @author : gengyy
 * @version : v2.0
 * @desc :
 * @since : 2019/12/18 18:46
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @PostConstruct
    public void beforeInit() {
        testService = service;
        url = localhostUrl;
    }

    @Value("${localhostUrl}")
    private String localhostUrl;

    private static String url;


    @Autowired
    private TestService service;

    private static TestService testService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求的URL
        String basePath = request.getContextPath();
        String path = request.getRequestURI();
        if (!doLoginInterceptor(path, basePath)) {//是否进行登陆拦截
            return true;
        }
        try {
            //统一拦截（查询当前session是否存在user）(这里user会在每次登陆成功后，写入session)
            User user = (User) request.getSession().getAttribute("user");
            String activeProfile = ProfileConfig.getActiveProfile();
            if(activeProfile.equals("dev")){
                user = new User();
                user.setId(1);
            }
            if (user == null) {
                String requestType = request.getHeader("X-Requested-With");
                if (requestType != null && requestType.equals("XMLHttpRequest")) {
                    response.setHeader("sessionstatus", "timeout");
                    response.getWriter().print("LoginTimeout");
                    return false;
                } else {
                    logger.info("尚未登录，跳转到登录界面");
                    response.sendRedirect(url + "/login");
                }
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;//如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
        //如果设置为true时，请求将会继续执行后面的操作
    }

    private boolean doLoginInterceptor(String path, String basePath) {
        path = path.substring(basePath.length());
        Set<String> notLoginPaths = new HashSet<>();
        //设置不进行登录拦截的路径：登录注册和验证码
        //notLoginPaths.add("/");
        notLoginPaths.add("/index");
        notLoginPaths.add("/toLogin");
        notLoginPaths.add("/login");
        notLoginPaths.add("/register");
        notLoginPaths.add("/register.html");
        notLoginPaths.add("/user/register");
        notLoginPaths.add("/user/login");
        notLoginPaths.add("/user/sendEmailMsg");
        notLoginPaths.add("/kaptcha.jpg");
        notLoginPaths.add("/kaptcha");

        if (notLoginPaths.contains(path)) return false;
        return true;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
//         System.out.println("执行了TestInterceptor的postHandle方法");
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        System.out.println("执行了TestInterceptor的afterCompletion方法");
    }
}
