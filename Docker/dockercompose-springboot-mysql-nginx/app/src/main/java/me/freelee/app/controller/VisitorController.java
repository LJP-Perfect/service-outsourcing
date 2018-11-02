package me.freelee.app.controller;

import me.freelee.app.dao.VisitorMapper;
import me.freelee.app.model.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * Date:2018/11/2
 *
 * @author:Lee
 */
@RestController
public class VisitorController {

    @Autowired
    VisitorMapper visitorMapper;

    @RequestMapping("/")
    public String index(HttpServletRequest request) {

        String ip=request.getRemoteAddr();
        Visitor visitor=visitorMapper.findByIp(ip);
        if(visitor==null){
            visitor=new Visitor();
            visitor.setIp(ip);
            visitor.setTimes(1);
            visitorMapper.insertSelective(visitor);

        }else{
            visitor.setTimes(visitor.getTimes()+1);
            visitorMapper.updateByPrimaryKeySelective(visitor);
        }
        return "I have been seen ip "+visitor.getIp()+":"+request.getLocalPort()+" "+visitor.getTimes()+" times.";
    }
}

