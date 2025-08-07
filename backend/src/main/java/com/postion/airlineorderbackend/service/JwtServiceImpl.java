package com.postion.airlineorderbackend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService{
    @Value("${jwt.secret}")
    private String SECRET;
    @Value("${jwt.expiration.ms}")
    private long VALID_TIME;

    /**
     * 生成Token操作
     *
     * @param claims   jwt设置数据
     * @return token
     */
    private String createToken(Map<String, Object> claims) {

        // 生成Key
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        // 生成Token
        return Jwts.builder()
            .claims(claims)
            .signWith(key,Jwts.SIG.HS256)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + +1000 * 60 * VALID_TIME))
            .compact();
    }

    /**
     * 生成token
     *
     * @param userDetails 用户名密码
     * @return token
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", userDetails.getUsername());
        claims.put("password", userDetails.getPassword());
        return createToken(claims);
    }

    /**
     * 从token取得用户名
     *
     * @param token token
     * @return 用户名
     *
     */
    @Override
    public String extractUserName(String token) {
        // 生成Key
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        // 从token取得用户名
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token).getPayload().get("userName").toString();
    }

    /**
     * 验证token
     *
     * @param token       token
     * @param userDetails UserDetails
     * @return token验证结果
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // 从token取得用户名
        final String userName = extractUserName(token);
        // 返回token验证结果
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 确认token是否超时
     *
     * @param token token
     * @return 超时结果
     */
    private boolean isTokenExpired(String token) {
        // 生成Key
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        // 返回确认token是否超时结果
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload().getExpiration().before(new Date());
    }
}
