package me.freelee.serviceapi.controller;

import me.freelee.commonutil.model.ResultMap;
import me.freelee.commonutil.util.JWTUtil;
import me.freelee.serviceapi.model.JWTToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 * Date:2018/11/5
 *
 * @author:Lee
 */
@Controller
public class LoginController {

    ResultMap resultMap=new ResultMap();

    /*
        在前端页面加入Authorization请求头，token
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultMap login(@RequestParam("username") String username,
                           @RequestParam("password") String password) {
        String realPassword = "123456";
        if (realPassword == null) {
            return resultMap.fail().message("用户不存在");
        } else if (!realPassword.equals(password)) {
            return resultMap.fail().message("密码错误");
        }
        Subject user = SecurityUtils.getSubject();
        try {
            String token= JWTUtil.sign(username);
            user.login(new JWTToken(token));
            return resultMap.success().message(token);
        } catch (RuntimeException e) {
            return resultMap.fail().message("error!!!");
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public ResultMap logout() {
        Subject subject = SecurityUtils.getSubject();
        //注销
        subject.logout();
        return resultMap.success().message("成功注销！");
    }


}
