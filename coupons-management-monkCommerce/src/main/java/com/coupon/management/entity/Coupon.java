package com.coupon.management.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Coupon code is required")
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank(message = "Coupon name is required")
    private String name;

    @NotBlank(message = "Coupon description is required")
    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private boolean active = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validUntil;

    @Min(value = 0, message = "Minimum cart value must be non-negative")
    private BigDecimal minimumCartValue;

    @Min(value = 1, message = "Maximum usage must be at least 1")
    private Integer maxUsage;

    @Min(value = 0, message = "Current usage must be non-negative")
    private Integer currentUsage = 0;

    @Min(value = 1, message = "Maximum discount amount must be at least 1")
    private BigDecimal maxDiscountAmount;

    // For product-wise coupons
    @ElementCollection
    @CollectionTable(name = "coupon_applicable_products",
            joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "product_id")
    private List<Long> applicableProductIds;

    // For BxGy coupons
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "coupon")
    @JsonManagedReference
    private List<BxGyRule> bxgyRules;

    @Min(value = 1, message = "Repetition limit must be at least 1")
    private Integer repetitionLimit = 1;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CouponType {
        CART_WISE,
        PRODUCT_WISE,
        BXGY
    }

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
} 