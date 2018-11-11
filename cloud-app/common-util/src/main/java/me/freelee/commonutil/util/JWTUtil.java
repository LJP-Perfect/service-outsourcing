package me.freelee.commonutil.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * Description:
 * Date:2018/11/6
 *
 * @author:Lee
 */
public class JWTUtil {

    // 过期时间5分钟
    private static final long EXPIRE_TIME = 60*60*1000;

    //设置私钥
    private static final String SECRET="freelee";

    /**
     * 校验token是否正确
     * @param token
     * @param username
     * @return
     */
    public static boolean verify(String token,String username){
        try{
            Algorithm algorithm=Algorithm.HMAC256(SECRET);
            JWTVerifier verifier= JWT.require(algorithm)
                    .withClaim("username",username)
                    .build();
            DecodedJWT jwt=verifier.verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static String getUsername(String token){
        try{
            DecodedJWT jwt=JWT.decode(token);
            return jwt.getClaim("username").asString();
        }catch (JWTDecodeException e){
            return null;
        }
    }

    public static String sign(String username){
        Date date = new Date(System.currentTimeMillis()+EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
    }

}
