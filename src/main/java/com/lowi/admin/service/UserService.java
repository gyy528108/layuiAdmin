package com.lowi.admin.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowi.admin.dao.IpDataDao;
import com.lowi.admin.dao.LoginLogDao;
import com.lowi.admin.dao.UserDao;
import com.lowi.admin.entity.IpData;
import com.lowi.admin.entity.LoginLog;
import com.lowi.admin.entity.User;
import com.lowi.admin.pojo.dto.UserDto;
import com.lowi.admin.utils.Md5Utils;
import com.lowi.admin.utils.Result;
import com.lowi.admin.utils.SendMailUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UserService.java
 * ==============================================
 * Copy right 2015-2017 by http://www.51lick.com
 * ----------------------------------------------
 * This is not a free software, without any authorization is not allowed to use and spread.
 * ==============================================
 *
 * @author : gengyy
 * @version : v2.0
 * @desc :
 * @since : 2019/12/19 11:16
 */
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IpDataDao ipDataDao;
    @Autowired
    private LoginLogDao loginLogDao;

    public Result registerUser(UserDto userDto) {
        Result responseResult = new Result();

        String email = stringRedisTemplate.opsForValue().get(userDto.getEmail() + "_email");
        if (email == null || email.equals("")) {
            responseResult.setCode(1);
            responseResult.setMsg("请发送验证码");
            return responseResult;
        }
        if (!email.equals(userDto.getValidaCode())) {
            responseResult.setCode(1);
            responseResult.setMsg("请输入正确的验证码");
            return responseResult;
        }

        User user = new User();
        user.setMobile(userDto.getMobile());
        User userPhone = userDao.selectOne(new QueryWrapper<>(user));
        if (userPhone != null) {
            responseResult.setCode(1);
            responseResult.setMsg("手机号已存在");
            return responseResult;
        }
        int insert = 0;
        try {
            user.setEmail(userDto.getEmail());
            user.setCreateTime(new Date());
            user.setIsDel(false);
            user.setUsername(userDto.getUserName());
            String md5Hex = Md5Utils.md5Hex(userDto.getPassword());
            user.setPassword(md5Hex);
            insert = userDao.insert(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (insert == 0) {
            responseResult.setCode(1);
            responseResult.setMsg("系统异常，请重试");
            return responseResult;
        }
        responseResult.setCode(0);
        responseResult.setMsg("注册成功");
        return responseResult;
    }

    @Transactional
    public Result<User> loginUser(UserDto userDto) {
        Result<User> responseResult = new Result<User>();
        String md5Hex = Md5Utils.md5Hex(userDto.getPassword());
        User userPo = userDao.loginUser(userDto.getMobile(), md5Hex);
        if (userPo == null) {
            responseResult.setCode(1);
            responseResult.setMsg("用户不存在或密码错误");
            return responseResult;
        }
        LoginLog loginLog = new LoginLog();
        String[] split = userDto.getLoginIp().split("\\.");
        if (split.length > 0) {
            String ipAddr = "";
            StringBuilder ip = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                ip.append(ipToStr(split[i]));
            }
            Long ipNum = Long.valueOf(ip.toString());
            List<IpData> ipAddrList = ipDataDao.getIpAddr(ipNum);
            if (ipAddrList.size() > 0) {
                String province = ipAddrList.get(0).getProvince();
                String city = ipAddrList.get(0).getCity();
                if (province != null) {
                    ipAddr = province;
                }
                if (city != null && !city.equals("")) {
                    ipAddr = ipAddr + "--" + city;
                }
            }
            loginLog.setLoginAddr(ipAddr);
            logger.info("登录地址-----------------" + ipAddr);
        }
        loginLog.setLoginTime(new Date());
        loginLog.setLoginIp(userDto.getLoginIp());
        loginLog.setUserId(userPo.getId());
        loginLogDao.insert(loginLog);

        userDto.setId(userPo.getId());
        userDto.setLastLoginIp(userPo.getLoginIp());
        userDao.updateLoginCount(userDto);
        responseResult.setCode(0);
        responseResult.setMsg("登录成功");
        responseResult.setData(userPo);
        return responseResult;
    }

    public String ipToStr(String num) {
        if (num.length() == 1) {
            return "00" + num;
        }
        if (num.length() == 2) {
            return "0" + num;
        }
        return num;
    }

    public Result sendEmailMsg(String email) {
        Result result = new Result();
        String s = stringRedisTemplate.opsForValue().get(email + "_increment");
        if (s != null) {
            Long increment = stringRedisTemplate.boundValueOps(email + "_increment").increment(1);
            if (increment > 4) {
                result.setCode(1);
                result.setMsg("请勿频繁发送（4/小时）");
                return result;
            }
        }
        String numbers = RandomUtil.randomNumbers(6);
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE>" + "<div bgcolor='#f1fcfa'  style='border:1px solid #d9f4ee; font-size:14px; line-height:22px; color:#005aa0;padding-left:1px;padding-top:5px;padding-bottom:5px;text-align:center;'>"
                + "<div style='width:950px;font-family:arial;'>欢迎使用Lowi后台管理服务，您的注册码为：<br/><h2 style='color:green'>" + numbers + "</h2><br/>本邮件由系统自动发出，请勿回复。<br/>感谢您的使用。<br/>祝您生活愉快。</div>"
                + "</div>");
        boolean sendMail = SendMailUtils.sendMail(email, "Lowi注册验证", sb.toString());
        if (sendMail) {
            stringRedisTemplate.opsForValue().set(email + "_email", numbers, 15, TimeUnit.MINUTES);
            if (s == null) {
                stringRedisTemplate.opsForValue().set(email + "_increment", "1", 1, TimeUnit.HOURS);
            }
            result.setCode(0);
            return result;
        } else {
            result.setCode(1);
            result.setMsg("请检验邮箱是否正确");
            return result;
        }
    }

    public static void main(String[] args) {
        String numbers = RandomUtil.randomNumbers(6);
        System.out.println("numbers = " + numbers);
    }

    public Result getUserLoginLog(Integer id, Integer page, Integer limit) {
        QueryWrapper<LoginLog> queryWrapper = new QueryWrapper<>();
        Page<LoginLog> pageInfo = new Page<>(page, limit);
        queryWrapper.select().eq("user_id", id).orderByDesc("login_time");
        IPage<LoginLog> loginLogIPage = loginLogDao.selectPage(pageInfo, queryWrapper);
        Result responseResult = new Result();
        responseResult.setCode(0);
        responseResult.setMsg("获取成功");
        responseResult.setData(loginLogIPage.getRecords());
        responseResult.setCount((int) loginLogIPage.getTotal());
        return responseResult;
    }
}
