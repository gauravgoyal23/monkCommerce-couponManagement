package com.coupon.management.dto;

import com.coupon.management.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotBlank(message = "Coupon name is required")
    private String name;

    @NotBlank(message = "Coupon description is required")
    private String description;

    @NotNull(message = "Coupon type is required")
    private Coupon.CouponType type;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount value must be non-negative+")
    private BigDecimal discountValue;

    @NotNull(message = "Discount type is required")
    private Coupon.DiscountType discountType;

    private boolean isActive = true;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    @DecimalMin(value = "0.0", message = "Minimum cart value must be non-negative")
    private BigDecimal minimumCartValue;

    @Min(value = 1, message = "Maximum usage must be at least 1")
    private Integer maxUsage;

    @DecimalMin(value = "1.0", message = "Maximum discount amount must be at least 1")
    private BigDecimal maxDiscountAmount;

    // For product-wise coupons
    private List<Long> applicableProductIds;

    // For BxGy coupons
    private List<BxGyRuleRequest> bxgyRules;

    @Min(value = 1, message = "Repetition limit must be at least 1")
    private Integer repetitionLimit = 1;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BxGyRuleRequest {
        @Min(value = 1, message = "Buy quantity must be at least 1")
        private Integer buyQuantity;

        @NotEmpty(message = "Buy product IDs cannot be empty")
        private List<Long> buyProductIds;

        @Min(value = 1, message = "Get quantity must be at least 1")
        private Integer getQuantity;

        @NotEmpty(message = "Get product IDs cannot be empty")
        private List<Long> getProductIds;

        @Min(value = 1, message = "Priority must be at least 1")
        private Integer priority = 1;
    }
}