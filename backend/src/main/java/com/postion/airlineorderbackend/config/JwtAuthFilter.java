package com.postion.airlineorderbackend.config;


import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt认证过滤器设置
 *
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 设置内部过滤器
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        // 从请求头部取得Authorization数据
        final String authHeader = request.getHeader("Authorization");

        // 没有取到Authorization数据或者数据不是以Bearer开头直接返回
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 从Authorization数据token取得
        final String token = authHeader.replace("Bearer ", "");

        // 从token中取得用户名
        String userName = "";
        try {
            userName = jwtService.extractUserName(token);
        }catch (ExpiredJwtException exception){
            request.setAttribute("jwtFilter.error",new BusinessException(HttpStatus.BAD_REQUEST, "身份验证过期"));
            request.getRequestDispatcher("/error/jwtFilter").forward(request,response);
            return;
        }catch (MalformedJwtException exception){
            request.setAttribute("jwtFilter.error",new BusinessException(HttpStatus.BAD_REQUEST, "JWT Token格式不对"));
            request.getRequestDispatcher("/error/jwtFilter").forward(request,response);
            return;
        }catch (SignatureException exception){
            //生成 token 和解析 token 使用的 SECRET 签名不一致就会报这个错误
            request.setAttribute("jwtFilter.error",new BusinessException(HttpStatus.BAD_REQUEST, "JWT 签名错误"));
            request.getRequestDispatcher("/error/jwtFilter").forward(request,response);
            return;
        }catch (UnsupportedJwtException exception){
            request.setAttribute("jwtFilter.error",new BusinessException(HttpStatus.BAD_REQUEST, "不支持的 Jwt"));
            request.getRequestDispatcher("/error/jwtFilter").forward(request,response);
            return;
        }catch (Exception e) {
            request.setAttribute("jwtFilter.error",new BusinessException(HttpStatus.BAD_REQUEST, "JWT 验证失败"));
            request.getRequestDispatcher("/error/jwtFilter").forward(request,response);
            return;
        }
        // 用户名不为空并且认证为null
        if (userName != null && !userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 用CustomUserDetailService来设置UserDetails
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            // token合法性确认
            if (jwtService.isTokenValid(token, userDetails)) {
                // authToken的定义
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                // Details的设置
                authToken.setDetails(new WebAuthenticationDetails(request));
                // 认证的设置
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 过滤器设置
        filterChain.doFilter(request, response);
    }
}
