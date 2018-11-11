package me.freelee.serviceapi.shiro;

import me.freelee.commonutil.model.User;
import me.freelee.commonutil.util.JWTUtil;
import me.freelee.serviceapi.model.JWTToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Description:
 * Date:2018/11/6
 *
 * @author:Lee
 */
public class MyRealm extends AuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username= JWTUtil.getUsername(principalCollection.toString());
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        //模拟数据库查询
        String perm="/user/add";
        if(username.equals("freelee")){
            info.addStringPermission(perm);
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token=(String)authenticationToken.getCredentials();
        String username= JWTUtil.getUsername(token);
        if(username==null || !JWTUtil.verify(token,username)){
            throw new UnauthenticatedException("Token invalid");
        }
        //模拟数据库查询
        User user=new User("freelee","123456");
        if(user.getPassword()==null){
            throw new UnauthenticatedException("User doesn't exist");
        }
        return new SimpleAuthenticationInfo(token,token,this.getName());
    }
}

