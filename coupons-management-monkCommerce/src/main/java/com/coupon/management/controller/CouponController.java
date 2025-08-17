package com.coupon.management.controller;

import com.coupon.management.dto.*;
import com.coupon.management.entity.Cart;
import com.coupon.management.entity.Coupon;
import com.coupon.management.exception.CouponException;
import com.coupon.management.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/createCoupon")
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        log.info("Creating coupon: {}", request.getCode());
        try {
            Coupon coupon = couponService.createCoupon(request);
            CouponResponse response = convertToCouponResponse(coupon);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CouponException e) {
            log.error("Error creating coupon: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/getAllCoupons")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        log.info("Fetching all coupons");
        List<Coupon> coupons = couponService.getAllCoupons();
        List<CouponResponse> responses = new ArrayList<>();
        for (Coupon coupon : coupons) {
            CouponResponse response = convertToCouponResponse(coupon);
            responses.add(response);
        }
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/getCouponById/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Long id) {
        log.info("Fetching coupon with id: {}", id);
        try {
            Coupon coupon = couponService.getCouponById(id);
            CouponResponse response = convertToCouponResponse(coupon);
            return ResponseEntity.ok(response);
        } catch (CouponException e) {
            log.error("Error fetching coupon: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/updateCouponById/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Long id,
                                                       @Valid @RequestBody CouponRequest request) {
        log.info("Updating coupon with id: {}", id);
        try {
            Coupon coupon = couponService.updateCouponById(id, request);
            CouponResponse response = convertToCouponResponse(coupon);
            return ResponseEntity.ok(response);
        } catch (CouponException e) {
            log.error("Error updating coupon: {}", e.getMessage());
            throw e;
        }
    }

    //deleteCouponById
    @DeleteMapping("/deleteCouponById/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        log.info("Deleting coupon with id: {}", id);
        try {
            couponService.deleteCouponById(id);
            return ResponseEntity.noContent().build();
        } catch (CouponException e) {
            log.error("Error deleting coupon: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<ApplicableCouponsResponse> getApplicableCoupons(@Valid @RequestBody CartRequest cartRequest) {
        log.info("Finding applicable coupons for cart");
        try {
            ApplicableCouponsResponse response = couponService.getApplicableCoupons(cartRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error finding applicable coupons: {}", e.getMessage());
            throw new CouponException("Error finding applicable coupons: " + e.getMessage());
        }
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartResponse> applyCoupon(@PathVariable Long id, @Valid @RequestBody CartRequest cartRequest) {
        log.info("Applying coupon {} to cart", id);
        try {
            Cart cart = couponService.applyCoupon(id, cartRequest);
            CartResponse response = CartResponse.fromCart(cart);
            return ResponseEntity.ok(response);
        } catch (CouponException e) {
            log.error("Error applying coupon: {}", e.getMessage());
            throw e;
        }
    }

    private CouponResponse convertToCouponResponse(Coupon coupon) {
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