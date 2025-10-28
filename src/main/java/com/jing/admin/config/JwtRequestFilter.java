package com.jing.admin.config;

import com.jing.admin.core.exception.BusinessException;
import com.jing.admin.core.exception.LoginException;
import com.jing.admin.model.domain.LoginUser;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private LoginUserUtil loginUserUtil;

    @Value("#{'${jwt.skip-paths}'.split(',')}")
    private List<String> skipPaths;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = skipPaths.stream().anyMatch(path::startsWith);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String userId = null;
        String jwtToken = null;
        try {
            // 只处理Bearer类型的token
            if (requestTokenHeader != null) {
                jwtToken = requestTokenHeader.startsWith("Bearer ") ? requestTokenHeader.substring(7) : requestTokenHeader;
                if (jwtTokenUtil.validateToken(jwtToken)) {
                    throw new LoginException("Token已过期或已过期");
                }
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    MDC.put("username", username);
                } catch (IllegalArgumentException e) {
                    log.error("Unable to get JWT Token", e);
                } catch (ExpiredJwtException e) {
                    log.error("JWT Token has expired", e);
                }
            } else {
                log.warn("JWT Token does not begin with Bearer String");
            }
            // 验证token
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                LoginUser user = loginUserUtil.getLoginUser(jwtToken);
                MDC.put("userId", user.getId());
                if (jwtTokenUtil.validateToken(jwtToken, user)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            chain.doFilter(request, response);
        } catch (LoginException e) {
            throw e;
        } finally {
            log.debug("Request completed, MDC cleared");
        }
    }

}