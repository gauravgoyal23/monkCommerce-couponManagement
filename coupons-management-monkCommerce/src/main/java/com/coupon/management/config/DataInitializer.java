package com.coupon.management.config;

import com.coupon.management.entity.BxGyRule;
import com.coupon.management.entity.Coupon;
import com.coupon.management.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");

        // Clear existing data
        couponRepository.deleteAll();

        // Create sample coupons
        createSampleCoupons();

        log.info("Sample data initialization completed!");
    }

    private void createSampleCoupons() {
        // Cart-wise coupon
        Coupon cartWiseCoupon = new Coupon();
        cartWiseCoupon.setCode("CART10");
        cartWiseCoupon.setName("10% Off on Cart");
        cartWiseCoupon.setDescription("Get 10% off on cart total above $100");
        cartWiseCoupon.setType(Coupon.CouponType.CART_WISE);
        cartWiseCoupon.setDiscountValue(BigDecimal.valueOf(10));
        cartWiseCoupon.setDiscountType(Coupon.DiscountType.PERCENTAGE);
        cartWiseCoupon.setActive(true);
        cartWiseCoupon.setValidFrom(LocalDateTime.now().minusDays(1));
        cartWiseCoupon.setValidUntil(LocalDateTime.now().plusMonths(1));
        cartWiseCoupon.setMinimumCartValue(BigDecimal.valueOf(100));
        cartWiseCoupon.setMaxUsage(100);
        cartWiseCoupon.setCurrentUsage(0);
        cartWiseCoupon.setMaxDiscountAmount(BigDecimal.valueOf(50));
        cartWiseCoupon.setRepetitionLimit(1);
        couponRepository.save(cartWiseCoupon);

        // Product-wise coupon
        Coupon productWiseCoupon = new Coupon();
        productWiseCoupon.setCode("PROD20");
        productWiseCoupon.setName("20% Off on Electronics");
        productWiseCoupon.setDescription("Get 20% off on electronics products");
        productWiseCoupon.setType(Coupon.CouponType.PRODUCT_WISE);
        productWiseCoupon.setDiscountValue(BigDecimal.valueOf(20));
        productWiseCoupon.setDiscountType(Coupon.DiscountType.PERCENTAGE);
        productWiseCoupon.setActive(true);
        productWiseCoupon.setValidFrom(LocalDateTime.now().minusDays(1));
        productWiseCoupon.setValidUntil(LocalDateTime.now().plusMonths(1));
        productWiseCoupon.setApplicableProductIds(Arrays.asList(1L, 2L, 3L));
        productWiseCoupon.setMaxUsage(50);
        productWiseCoupon.setCurrentUsage(0);
        productWiseCoupon.setMaxDiscountAmount(BigDecimal.valueOf(100));
        productWiseCoupon.setRepetitionLimit(1);
        couponRepository.save(productWiseCoupon);

        // BxGy coupon
        Coupon bxgyCoupon = new Coupon();
        bxgyCoupon.setCode("B2G1");
        bxgyCoupon.setName("Buy 2 Get 1 Free");
        bxgyCoupon.setDescription("Buy 2 products from selected items, get 1 free");
        bxgyCoupon.setType(Coupon.CouponType.BXGY);
        bxgyCoupon.setDiscountValue(BigDecimal.valueOf(0));
        bxgyCoupon.setDiscountType(Coupon.DiscountType.FIXED_AMOUNT);
        bxgyCoupon.setActive(true);
        bxgyCoupon.setValidFrom(LocalDateTime.now().minusDays(1));
        bxgyCoupon.setValidUntil(LocalDateTime.now().plusMonths(1));
        bxgyCoupon.setMaxUsage(30);
        bxgyCoupon.setCurrentUsage(0);
        bxgyCoupon.setRepetitionLimit(3);

        // Create BxGy rules
        BxGyRule rule1 = new BxGyRule();
        rule1.setCoupon(bxgyCoupon);
        rule1.setBuyQuantity(2);
        rule1.setBuyProductIds(Arrays.asList(1L, 2L, 3L));
        rule1.setGetQuantity(1);
        rule1.setGetProductIds(Arrays.asList(4L, 5L));
        rule1.setPriority(1);

        BxGyRule rule2 = new BxGyRule();
        rule2.setCoupon(bxgyCoupon);
        rule2.setBuyQuantity(3);
        rule2.setBuyProductIds(Arrays.asList(6L, 7L));
        rule2.setGetQuantity(1);
        rule2.setGetProductIds(Arrays.asList(8L));
        rule2.setPriority(2);

        bxgyCoupon.setBxgyRules(Arrays.asList(rule1, rule2));
        couponRepository.save(bxgyCoupon);

        // Fixed amount coupon
        Coupon fixedAmountCoupon = new Coupon();
        fixedAmountCoupon.setCode("SAVE20");
        fixedAmountCoupon.setName("Save $20");
        fixedAmountCoupon.setDescription("Save $20 on orders above $150");
        fixedAmountCoupon.setType(Coupon.CouponType.CART_WISE);
        fixedAmountCoupon.setDiscountValue(BigDecimal.valueOf(20));
        fixedAmountCoupon.setDiscountType(Coupon.DiscountType.FIXED_AMOUNT);
        fixedAmountCoupon.setActive(true);
        fixedAmountCoupon.setValidFrom(LocalDateTime.now().minusDays(1));
        fixedAmountCoupon.setValidUntil(LocalDateTime.now().plusMonths(1));
        fixedAmountCoupon.setMinimumCartValue(BigDecimal.valueOf(150));
        fixedAmountCoupon.setMaxUsage(75);
        fixedAmountCoupon.setCurrentUsage(0);
        fixedAmountCoupon.setMaxDiscountAmount(BigDecimal.valueOf(20));
        fixedAmountCoupon.setRepetitionLimit(1);
        couponRepository.save(fixedAmountCoupon);

        log.info("Created {} sample coupons", couponRepository.count());
    }
}