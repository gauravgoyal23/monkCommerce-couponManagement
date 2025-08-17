package com.coupon.management.dto;

import com.coupon.management.entity.Coupon;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CouponDataDto {
    private String code;
    private String name;
    private String description;
    private Coupon.CouponType type;
    private BigDecimal discountValue;
    private Coupon.DiscountType discountType;
    private boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime validFrom;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime validUntil;

    private BigDecimal minimumCartValue;
    private Integer maxUsage;
    private Integer currentUsage;
    private BigDecimal maxDiscountAmount;
    private Integer repetitionLimit;
    private List<Long> applicableProductIds;
    private List<BxGyRuleDataDto> bxgyRules;

    @Data
    public static class BxGyRuleDataDto {
        private Integer buyQuantity;
        private List<Long> buyProductIds;
        private Integer getQuantity;
        private List<Long> getProductIds;
        private Integer priority;
    }
}
