package com.shelfconnect.dto.res;

import com.shelfconnect.dto.AddressDTO;
import com.shelfconnect.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class FullProfileRes {
    private String name;
    private String email;
    private String phone_no;
    private String avatar;
    private boolean isVerified;
    private boolean isActive;
    private User.Role role;
    private List<AddressDTO> addresses;

    public static FullProfileRes from(User user) {
        return FullProfileRes.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone_no(user.getPhone_no())
                .avatar(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
                .isActive(user.isActive())
                .isVerified(user.isVerified())
                .addresses(
                        user.getAddresses()
                                .stream()
                                .map(AddressDTO::from)
                                .collect(Collectors.toList())
                )
                .role(user.getRole())
                .build();
    }


}
