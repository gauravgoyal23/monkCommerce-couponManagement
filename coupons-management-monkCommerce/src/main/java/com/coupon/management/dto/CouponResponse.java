package com.coupon.management.dto;

import com.coupon.management.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Coupon.CouponType type;
    private BigDecimal discountValue;
    private Coupon.DiscountType discountType;
    private boolean isActive;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private BigDecimal minimumCartValue;
    private Integer maxUsage;
    private Integer currentUsage;
    private BigDecimal maxDiscountAmount;
    private Integer repetitionLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Calculated discount for applicable coupons
    private BigDecimal calculatedDiscount;
    private String discountDescription;

    public static CouponResponse fromCoupon(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setCode(coupon.getCode());
        response.setName(coupon.getName());
        response.setDescription(coupon.getDescription());
        response.setType(coupon.getType());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setDiscountType(coupon.getDiscountType());
        response.setActive(coupon.isActive());
        response.setValidFrom(coupon.getValidFrom());
        response.setValidUntil(coupon.getValidUntil());
        response.setMinimumCartValue(coupon.getMinimumCartValue());
        response.setMaxUsage(coupon.getMaxUsage());
        response.setCurrentUsage(coupon.getCurrentUsage());
        response.setMaxDiscountAmount(coupon.getMaxDiscountAmount());
        response.setRepetitionLimit(coupon.getRepetitionLimit());
        response.setCreatedAt(coupon.getCreatedAt());
        response.setUpdatedAt(coupon.getUpdatedAt());
        return response;
    }
} 