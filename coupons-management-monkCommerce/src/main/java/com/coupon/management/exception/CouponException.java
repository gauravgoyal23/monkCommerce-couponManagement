package com.coupon.management.exception;

public class CouponException extends RuntimeException {

    public CouponException(String message) {
        super(message);
    }

    public CouponException(String message, Throwable cause) {
        super(message, cause);
    }
} 