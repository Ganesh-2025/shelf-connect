package com.shelfconnect.dto;

import com.shelfconnect.model.SharedContactDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SharedContactDetailsDTO {
    private Long id;
    private Long fromID;
    private Long toID;
    private SharedContactDetails.Status status;
    private String details;

    public static SharedContactDetailsDTO from(SharedContactDetails sharedContactDetails){
        return SharedContactDetailsDTO.builder()
                .id(sharedContactDetails.getID())
                .fromID(sharedContactDetails.getFrom().getId())
                .toID(sharedContactDetails.getTo().getId())
                .status(sharedContactDetails.getStatus())
                .details(sharedContactDetails.getDetails())
                .build();
    }

}
