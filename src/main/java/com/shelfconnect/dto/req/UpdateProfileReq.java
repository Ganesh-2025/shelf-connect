package com.shelfconnect.dto.req;

import com.shelfconnect.dto.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UpdateProfileReq {
    @Size(min = 1, max = 50)
    private String name;
    @Email
    private String email;
    @Size(min = 1, max = 50)
    private String phoneNo;
    @Valid
    private List<AddressDTO> addresses = new ArrayList<>();
}
