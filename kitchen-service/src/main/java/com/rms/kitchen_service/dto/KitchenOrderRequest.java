package com.rms.kitchen_service.dto;

import java.util.Date;

import com.rms.kitchen_service.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenOrderRequest {

    private Long orderID;

    private Long tableID;

    private Long userID;

    private String userName;

    private StatusEnum status;

    private String specialInstructuction;

    private Date createdAt;

    private Date updatedAt;

}
