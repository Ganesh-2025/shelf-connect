package com.shelfconnect.dto.req;

import com.shelfconnect.model.User;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterReq {
    @NotNull
    @Size(min = 1, max = 20)
    private final String name;

    @NotNull
    @Email
    private final String email;

    @NotNull
    @Size(min = 8, max = 30, message = "invalid size")
    private final String password;

    @NotNull
    @Size(min = 8, max = 30, message = "invalid size")
    private final String confirmPassword;

    private final User.Role role;

    @AssertTrue(message = "Passwords do not match")
    private boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
