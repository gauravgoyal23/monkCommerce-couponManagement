package com.coupon.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicableCouponsResponse {
    private List<CouponResponse> applicableCoupons;
    private int totalApplicableCoupons;
} 