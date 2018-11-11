package me.freelee.serviceapi.model;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Description:
 * Date:2018/11/6
 *
 * @author:Lee
 */
public class JWTToken implements AuthenticationToken {

    private String token;

    public JWTToken(String token){
        this.token=token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
