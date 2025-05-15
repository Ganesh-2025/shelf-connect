package com.shelfconnect.controller;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import com.shelfconnect.dto.req.LoginReq;
import com.shelfconnect.dto.req.RegisterReq;
import com.shelfconnect.model.User;
import com.shelfconnect.security.jwt.JWTUtil;
import com.shelfconnect.security.user.UserDetails;
import com.shelfconnect.service.impl.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@RestController()
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(
            @RequestBody @Valid LoginReq loginReq,
            HttpServletResponse response,
            @Value("${app.jwt_expiration}") Duration jwtExpiration
    ) {

//        AUTHENTICATION
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);

//        TOKEN GENERATION FROM EMAIL
        String jwtToken = jwtUtil.generateTokenFromUsername(userDetails.getUsername());

//        BUILD COOKIE
        Cookie cookie = new Cookie("JWT", URLEncoder.encode(jwtToken, StandardCharsets.UTF_8));
        cookie.setMaxAge((int) jwtExpiration.toSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);

        return new ResponseEntity<>(
                APIResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .message("Login Successful")
                        .data(Map.of("token", jwtToken))
                        .build(),
                HttpStatus.OK
        );
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse> register(
            @NotNull @Valid @RequestBody RegisterReq registerReq,
            HttpServletResponse response,
            @Value("${app.jwt_expiration}") Duration jwtExpiration
    ) {

        User user = userService.createUser(
                User.builder()
                        .name(registerReq.getName())
                        .email(registerReq.getEmail())
                        .password(registerReq.getPassword())
                        .role(User.Role.USER)
                        .isActive(true)
                        .isVerified(false)
                        .build()
        ).orElseThrow(() -> APIException.forInternalServerError("Unable to Create User. try again later!"));

        String jwtToken = jwtUtil.generateTokenFromUsername(user.getEmail());

        Cookie cookie = new Cookie("JWT", URLEncoder.encode(jwtToken, StandardCharsets.UTF_8));
        cookie.setMaxAge((int) jwtExpiration.toSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                APIResponse.builder()
                        .statusCode(HttpStatus.CREATED)
                        .status(Status.SUCCESS)
                        .message("Registration Successful")
                        .data(Map.of("token", jwtToken))
                        .build()
        );
    }

    @GetMapping("/logout")
    public ResponseEntity<APIResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(
                APIResponse.builder()
                        .status(Status.SUCCESS)
                        .statusCode(HttpStatus.OK)
                        .message("logout successfully")
                        .build()
        );
    }
}
