package com.wx.cloudnotes.controller;

import com.wx.cloudnotes.common.Constants;
import com.wx.cloudnotes.common.WxResult;
import com.wx.cloudnotes.domain.User;
import com.wx.cloudnotes.utils.log.LogUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {


    @RequestMapping("/login")
    public String isLogin() {
        /**判断没有登陆跳到登陆页面*/
        System.out.println("");
        return "/login/login";
    }


    /**
     * 用户登录的方法
     *
     * @param user
     * @return
     */
    @RequestMapping("/userLogin")
    @ResponseBody
    public WxResult login(User user, HttpServletRequest request) {
        /**经过一番验证登陆成功*/
        WxResult wxResult = new WxResult();
        try {
            if (user.getUser_name() == null || "".equals(user.getUser_name()) || user.getUser_password() == null || "".equals(user.getUser_name())) {
                wxResult.setStatus(500);
                wxResult.setMsg("用户名或者密码错误！");
                return wxResult;
            }
        } catch (Exception e) {
            LogUtils.getExceptionLogger().error("登录失败:" + "userName=" + user.getUser_name(), e);

        }
        HttpSession session = request.getSession();//获取session并将userName存入session对象
        session.setAttribute(Constants.USER_INFO, user.getUser_name());
        wxResult.setStatus(200);
        return wxResult;
    }

    /**
     * 注销登陆
     * @return
     */
    @RequestMapping("/cancelLogin")
    public void cancelLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().setAttribute(Constants.USER_INFO,null);
        response.sendRedirect("/login");
    }

}
