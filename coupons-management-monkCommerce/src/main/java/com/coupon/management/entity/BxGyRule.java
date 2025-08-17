package com.coupon.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.List;

@Entity
@Table(name = "bxgy_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BxGyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    @JsonIgnore
    private Coupon coupon;

    @Min(value = 1, message = "Buy quantity must be at least 1")
    @Column(nullable = false)
    private Integer buyQuantity;

    @ElementCollection
    @CollectionTable(name = "bxgy_buy_products",
            joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "product_id")
    private List<Long> buyProductIds;

    @Min(value = 1, message = "Get quantity must be at least 1")
    @Column(nullable = false)
    private Integer getQuantity;

    @ElementCollection
    @CollectionTable(name = "bxgy_get_products",
            joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "product_id")
    private List<Long> getProductIds;

    @Min(value = 1, message = "Priority must be at least 1")
    @Column(nullable = false)
    private Integer priority = 1;
} 