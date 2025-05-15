package com.shelfconnect.dto.res;

import com.shelfconnect.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoRes {
    private String name;
    private String email;
    private String avatar;

    public static UserInfoRes fromUser(User user) {
        UserInfoRes userInfoRes = UserInfoRes.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        if (user.getAvatar() != null)
            userInfoRes.setAvatar(user.getAvatar().getUrl());
        return userInfoRes;
    }
}
