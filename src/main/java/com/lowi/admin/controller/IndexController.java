package com.lowi.admin.controller;

import com.lowi.admin.entity.User;
import com.lowi.admin.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * IndexController.java
 * ==============================================
 * Copy right 2015-2017 by http://www.51lick.com
 * ----------------------------------------------
 * This is not a free software, without any authorization is not allowed to use and spread.
 * ==============================================
 *
 * @author : gengyy
 * @version : v2.0
 * @desc :
 * @since : 2019/12/13 10:43
 */
@Controller
@RequestMapping
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    public User getUser() {
        User user = testService.getUser();
        return user;
    }

    @RequestMapping()
    public String index() {
        logger.info("-------------index------------");
        return "index";
    }

    @RequestMapping("/login")
    public String toLogin() {
        logger.info("===111-------------login------------");
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "/login";
    }

    @RequestMapping("/user/toTable")
    public String toUserInfo() {
        return "/user/table";
    }

}
