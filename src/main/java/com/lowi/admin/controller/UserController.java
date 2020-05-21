package com.lowi.admin.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lowi.admin.entity.User;
import com.lowi.admin.pojo.dto.UserDto;
import com.lowi.admin.service.UserService;
import com.lowi.admin.utils.IPUtil;
import com.lowi.admin.utils.Result;
import com.lowi.admin.validation.group.UserGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * UserController.java
 * ==============================================
 * Copy right 2015-2017 by http://www.51lick.com
 * ----------------------------------------------
 * This is not a free software, without any authorization is not allowed to use and spread.
 * ==============================================
 *
 * @author : gengyy
 * @version : v2.0
 * @desc :
 * @since : 2019/12/19 10:48
 */
@RestController
@RequestMapping("user")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public Result register(@RequestBody @Validated(UserGroup.UserRegister.class) UserDto userDto, Errors errors) {
        if (errors.hasErrors()) {
            return Result.getError(errors);
        }
        return userService.registerUser(userDto);
    }

    @RequestMapping("/login")
    public Result login(HttpServletRequest request, @RequestBody @Validated(UserGroup.UserLogin.class) UserDto userDto, Errors errors, HttpSession session) {
        if (errors.hasErrors()) {
            return Result.getError(errors);
        }

        String requestUrlIP = request.getServerName();
        String userIpAddr = request.getRemoteAddr();
        logger.info("userIpAddr = " + userIpAddr);
        logger.info("requestUrlIP = " + requestUrlIP);
        userDto.setLoginIp(userIpAddr);
        Result<User> responseResult = userService.loginUser(userDto);
        if (responseResult.getCode() == 0) {
            session.setAttribute("user", responseResult.getData());
        }
        return responseResult;
    }

    @RequestMapping("/getUserInfo")
    public Result getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Result<User> responseResult = new Result<User>();
        responseResult.setCode(0);
        responseResult.setMsg("获取成功");
        responseResult.setData(user);
        return responseResult;
    }

    @RequestMapping("/getInfo")
    public Result getInfo(Integer page, Integer limit, Integer sex, String city, String type) {
        System.out.println("page = " + page);
        System.out.println("limit = " + limit);
        System.out.println("sex = " + sex);
        System.out.println("city = " + city);
        System.out.println("type = " + type);
        JSONArray objects = JSONUtil.parseArray("[\n" +
                "    {\n" +
                "      \"id\": 10000,\n" +
                "      \"username\": \"user-0\",\n" +
                "      \"sex\": \"女\",\n" +
                "      \"city\": \"城市-0\",\n" +
                "      \"sign\": \"签名-0\",\n" +
                "      \"experience\": 255,\n" +
                "      \"logins\": 24,\n" +
                "      \"wealth\": 82830700,\n" +
                "      \"classify\": \"作家\",\n" +
                "      \"score\": 57\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10001,\n" +
                "      \"username\": \"user-1\",\n" +
                "      \"sex\": \"男\",\n" +
                "      \"city\": \"城市-1\",\n" +
                "      \"sign\": \"签名-1\",\n" +
                "      \"experience\": 884,\n" +
                "      \"logins\": 58,\n" +
                "      \"wealth\": 64928690,\n" +
                "      \"classify\": \"词人\",\n" +
                "      \"score\": 27\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10002,\n" +
                "      \"username\": \"user-2\",\n" +
                "      \"sex\": \"女\",\n" +
                "      \"city\": \"城市-2\",\n" +
                "      \"sign\": \"签名-2\",\n" +
                "      \"experience\": 650,\n" +
                "      \"logins\": 77,\n" +
                "      \"wealth\": 6298078,\n" +
                "      \"classify\": \"酱油\",\n" +
                "      \"score\": 31\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10003,\n" +
                "      \"username\": \"user-3\",\n" +
                "      \"sex\": \"女\",\n" +
                "      \"city\": \"城市-3\",\n" +
                "      \"sign\": \"签名-3\",\n" +
                "      \"experience\": 362,\n" +
                "      \"logins\": 157,\n" +
                "      \"wealth\": 37117017,\n" +
                "      \"classify\": \"诗人\",\n" +
                "      \"score\": 68\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10004,\n" +
                "      \"username\": \"user-4\",\n" +
                "      \"sex\": \"男\",\n" +
                "      \"city\": \"城市-4\",\n" +
                "      \"sign\": \"签名-4\",\n" +
                "      \"experience\": 807,\n" +
                "      \"logins\": 51,\n" +
                "      \"wealth\": 76263262,\n" +
                "      \"classify\": \"作家\",\n" +
                "      \"score\": 6\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10005,\n" +
                "      \"username\": \"user-5\",\n" +
                "      \"sex\": \"女\",\n" +
                "      \"city\": \"城市-5\",\n" +
                "      \"sign\": \"签名-5\",\n" +
                "      \"experience\": 173,\n" +
                "      \"logins\": 68,\n" +
                "      \"wealth\": 60344147,\n" +
                "      \"classify\": \"作家\",\n" +
                "      \"score\": 87\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10006,\n" +
                "      \"username\": \"user-6\",\n" +
                "      \"sex\": \"女\",\n" +
                "      \"city\": \"城市-6\",\n" +
                "      \"sign\": \"签名-6\",\n" +
                "      \"experience\": 982,\n" +
                "      \"logins\": 37,\n" +
                "      \"wealth\": 57768166,\n" +
                "      \"classify\": \"作家\",\n" +
                "      \"score\": 34\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10007,\n" +
                "      \"username\": \"user-7\",\n" +
                "      \"sex\": \"男\",\n" +
                "      \"city\": \"城市-7\",\n" +
                "      \"sign\": \"签名-7\",\n" +
                "      \"experience\": 727,\n" +
                "      \"logins\": 150,\n" +
                "      \"wealth\": 82030578,\n" +
                "      \"classify\": \"作家\",\n" +
                "      \"score\": 28\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10008,\n" +
                "      \"username\": \"user-8\",\n" +
                "      \"sex\": \"男\",\n" +
                "      \"city\": \"城市-8\",\n" +
                "      \"sign\": \"签名-8\",\n" +
                "      \"experience\": 951,\n" +
                "      \"logins\": 133,\n" +
                "      \"wealth\": 16503371,\n" +
                "      \"classify\": \"词人\",\n" +
                "      \"score\": 14\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 10009,\n" +
                "      \"username\": \"user-9\",\n" +
                "      \"sex\": \"女\",\n" +
                "      \"city\": \"城市-9\",\n" +
                "      \"sign\": \"签名-9\",\n" +
                "      \"experience\": 484,\n" +
                "      \"logins\": 25,\n" +
                "      \"wealth\": 86801934,\n" +
                "      \"classify\": \"词人\",\n" +
                "      \"score\": 75\n" +
                "    }\n" +
                "  ]");
        Result result = new Result();
        result.setCode(0);
        result.setMsg("成功");
        result.setCount(9);
        result.setData(objects);

        return result;
    }

    @RequestMapping("/logout")
    public Result logout(HttpSession session) {
        session.removeAttribute("user");
        Result result = new Result();
        result.setCode(0);
        return result;
    }

    @RequestMapping("/sendEmailMsg")
    public Result sendEmailMsg(String email) {
        return userService.sendEmailMsg(email);
    }

}
