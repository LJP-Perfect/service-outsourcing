package me.freelee.serviceapi.controller;

import me.freelee.commonutil.model.ResultMap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * Date:2018/11/10
 *
 * @author:Lee
 */
@RestController
public class UserController {

    private ResultMap resultMap;

    @RequestMapping("/user/add")
    @RequiresPermissions("/user/add")
    @ResponseBody
    public String userAdd(){
        return "user/add";
    }

    @RequestMapping("/user/delete")
    @RequiresPermissions("/user/delete")
    @ResponseBody
    public String userDelete(){
        return "user/delete";
    }
}
