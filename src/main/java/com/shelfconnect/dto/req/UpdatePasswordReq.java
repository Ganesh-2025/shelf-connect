package com.shelfconnect.dto.req;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordReq {
    @NotNull(message = "old password required")
    @Size(min = 8, max = 30)
    private String oldPassword;

    @NotNull(message = "new password required")
    @Size(min = 8, max = 30)
    private String newPassword;

    @AssertTrue(message = "old password and new password is same")
    public boolean isEqual() {
        return oldPassword == null || !oldPassword.equals(newPassword);
    }
}
