package com.shelfconnect.dto;

import com.shelfconnect.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddressDTO {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 50)
    private String area;
    @NotBlank
    @Size(min = 1, max = 50)
    private String city;
    @NotBlank
    @Size(min = 1, max = 50)
    private String state;
    @NotBlank
    @Size(min = 1, max = 50)
    private String country;
    @NotBlank
    @Size(min = 1, max = 6)
    private String pincode;

    public static AddressDTO from(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .area(address.getArea())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .pincode(address.getPincode())
                .build();
    }
    public static List<AddressDTO> from(List<Address> addressList){
        return addressList.stream()
                .map(AddressDTO::from)
                .toList();
    }
}
