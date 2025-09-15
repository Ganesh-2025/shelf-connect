package com.shelfconnect.constant;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public interface URL {
    AntPathRequestMatcher[] PUBLIC = {
            new AntPathRequestMatcher("/api/auth/**"),
            new AntPathRequestMatcher("/api/book/book-categories/**","GET"),
            new AntPathRequestMatcher("/api/book/**","GET")
    };
    AntPathRequestMatcher[] USER = {
            new AntPathRequestMatcher("/api/user/**"),
            new AntPathRequestMatcher("/api/book/**"),
            new AntPathRequestMatcher("/api/order/**"),
            new AntPathRequestMatcher("/api/image/**"),
            new AntPathRequestMatcher("/api/order/**")
    };
    AntPathRequestMatcher[] ADMIN = {new AntPathRequestMatcher("/api/admin/**")};
}
