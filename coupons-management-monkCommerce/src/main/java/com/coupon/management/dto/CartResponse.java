package com.coupon.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal finalPrice;
    private CouponResponse appliedCoupon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartResponse fromCart(com.coupon.management.entity.Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalPrice(cart.getTotalPrice());
        response.setTotalDiscount(cart.getTotalDiscount());
        response.setFinalPrice(cart.getFinalPrice());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        // Convert cart items
        if (cart.getItems() != null) {
            List<CartItemResponse> itemResponses = cart.getItems().stream()
                    .map(CartItemResponse::fromCartItem)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        // Convert applied coupon
        if (cart.getAppliedCoupon() != null) {
            response.setAppliedCoupon(CouponResponse.fromCoupon(cart.getAppliedCoupon()));
        }

        return response;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal discount;
        private BigDecimal totalDiscount;
        private BigDecimal finalPrice;

        public static CartItemResponse fromCartItem(com.coupon.management.entity.CartItem cartItem) {
            CartItemResponse response = new CartItemResponse();
            response.setId(cartItem.getId());
            response.setProductId(cartItem.getProductId());
            response.setProductName(cartItem.getProductName());
            response.setQuantity(cartItem.getQuantity());
            response.setPrice(cartItem.getPrice());
            response.setDiscount(cartItem.getDiscount());
            response.setTotalDiscount(cartItem.getTotalDiscount());
            response.setFinalPrice(cartItem.getFinalPrice());
            return response;
        }
    }
} 