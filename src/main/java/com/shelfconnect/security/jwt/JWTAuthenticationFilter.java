package com.shelfconnect.security.jwt;

import com.shelfconnect.constant.URL;
import com.shelfconnect.security.user.CustomUserDetailsService;
import com.shelfconnect.security.user.UserDetails;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(URL.PUBLIC).anyMatch(url -> url.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromHeader(request).orElseThrow(() -> new BadCredentialsException("Invalid JWT token"));
            System.out.println("header " + token);
            if (jwtUtil.validateJwtToken(token)) {
                String email = jwtUtil.getUserNameFromJwtToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authtoken);
            }
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            ex.printStackTrace();
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }

    private Optional<String> getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (isTokenValid(bearerToken)) {
            return Optional.of(bearerToken.substring(7)); // Remove Bearer prefix
        }
        return Optional.empty();
    }

    private Optional<String> getJwtFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> {
                            System.out.println(cookie.getName() + " " + cookie.getValue());
                            return cookie.getName().equals("JWT") && isTokenValid(cookie.getValue());
                        })
                        .map(Cookie::getValue)
                        .findFirst());
    }

    private boolean isTokenValid(String token) {
        return token != null && token.length() > 7 && token.startsWith("Bearer ");
    }
}
