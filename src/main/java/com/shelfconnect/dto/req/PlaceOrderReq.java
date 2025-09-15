package com.shelfconnect.dto.req;

import com.shelfconnect.dto.ItemDTO;
import lombok.Data;

@Data
public class PlaceOrderReq {
    private ItemDTO itemDTO;
}
