package com.shelfconnect.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginReq {
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
}
