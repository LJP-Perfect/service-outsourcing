package me.freelee.serviceapi.filter;

import me.freelee.serviceapi.model.JWTToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Description:
 * Date:2018/11/6
 *
 * @author:Lee
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {

    private Logger logger= LoggerFactory.getLogger(JWTFilter.class);

    /**
     * 如果带有token，则对token检查，否则直接通过
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if(isLoginAttempt(request,response)){
            try{
                //检查token
                executeLogin(request,response);
                return true;
            }catch (Exception e){
                //token错误
                responseError(response,e.getMessage());
            }
        }
        //请求头不存在Token，可能是游客访问状态，则无需检查token
        return true;
    }

    /**
     * 检测header是否包含有Token
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req= (HttpServletRequest) request;
        String token=req.getHeader("Authorization");
        return token!=null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req= (HttpServletRequest) request;
        String token=req.getHeader("Authorization");
        JWTToken jwtToken=new JWTToken(token);
        //提交给realm进行验证
        getSubject(request,response).login(jwtToken);
        return true;
    }

    /**
     * 为跨域提供支持
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin",httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        //跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if(httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())){
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request,response);
    }

    /**
     * 将非法请求跳转到/unauthorized/**
     *
     */
    private void responseError(ServletResponse response,String message){
        try{
            HttpServletResponse httpServletResponse= (HttpServletResponse) response;
            message= URLEncoder.encode(message,"UTF-8");
            httpServletResponse.sendRedirect("/403/"+message);
        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }
}