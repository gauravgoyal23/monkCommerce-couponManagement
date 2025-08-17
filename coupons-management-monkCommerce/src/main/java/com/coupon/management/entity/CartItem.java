package com.coupon.management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference
    private Cart cart;

    @Column(nullable = false)
    private Long productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false)
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount must be non-negative")
    @Column(nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Total discount must be non-negative")
    @Column(nullable = false)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @PrePersist
    @PreUpdate
    public void calculateFinalPrice() {
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        this.totalDiscount = discount.multiply(BigDecimal.valueOf(quantity));
        this.finalPrice = totalPrice.subtract(this.totalDiscount);
    }
} 