package com.coupon.management.service;

import com.coupon.management.dto.ApplicableCouponsResponse;
import com.coupon.management.dto.CartRequest;
import com.coupon.management.dto.CouponRequest;
import com.coupon.management.dto.CouponResponse;
import com.coupon.management.entity.BxGyRule;
import com.coupon.management.entity.Cart;
import com.coupon.management.entity.CartItem;
import com.coupon.management.entity.Coupon;
import com.coupon.management.exception.CouponException;
import com.coupon.management.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    //createCoupon
    public Coupon createCoupon(CouponRequest request) {
        log.info("Creating coupon with code: {}", request.getCode());

        if (couponRepository.existsByCode(request.getCode())) {
            throw new CouponException("Coupon with code " + request.getCode() + " already exists");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setType(request.getType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setActive(request.isActive());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        coupon.setMinimumCartValue(request.getMinimumCartValue());
        coupon.setMaxUsage(request.getMaxUsage());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setApplicableProductIds(request.getApplicableProductIds());
        coupon.setRepetitionLimit(request.getRepetitionLimit());

        // create BxGy rules
        if (request.getBxgyRules() != null && !request.getBxgyRules().isEmpty()) {
            List<BxGyRule> bxgyRules = request.getBxgyRules().stream()
                    .map(ruleRequest -> {
                        BxGyRule rule = new BxGyRule();
                        rule.setCoupon(coupon);
                        rule.setBuyQuantity(ruleRequest.getBuyQuantity());
                        rule.setBuyProductIds(ruleRequest.getBuyProductIds());
                        rule.setGetQuantity(ruleRequest.getGetQuantity());
                        rule.setGetProductIds(ruleRequest.getGetProductIds());
                        rule.setPriority(ruleRequest.getPriority());
                        return rule;
                    })
                    .collect(Collectors.toList());
            coupon.setBxgyRules(bxgyRules);
        }
        return couponRepository.save(coupon);
    }

    //getAllCoupons
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    //getCouponById
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new CouponException("Coupon not found with id: " + id));
    }

    //updateCouponById
    public Coupon updateCouponById(Long id, CouponRequest request) {
        log.info("Updating coupon with id: {}", id);

        Coupon existingCoupon = getCouponById(id);
        // Check if updated coupon details are same with exisitng
        if (!existingCoupon.getCode().equals(request.getCode()) &&
                couponRepository.existsByCode(request.getCode())) {
            throw new CouponException("Coupon with code " + request.getCode() + " already exists");
        }
        existingCoupon.setCode(request.getCode());
        existingCoupon.setName(request.getName());
        existingCoupon.setDescription(request.getDescription());
        existingCoupon.setType(request.getType());
        existingCoupon.setDiscountValue(request.getDiscountValue());
        existingCoupon.setDiscountType(request.getDiscountType());
        existingCoupon.setActive(request.isActive());
        existingCoupon.setValidFrom(request.getValidFrom());
        existingCoupon.setValidUntil(request.getValidUntil());
        existingCoupon.setMinimumCartValue(request.getMinimumCartValue());
        existingCoupon.setMaxUsage(request.getMaxUsage());
        existingCoupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        existingCoupon.setApplicableProductIds(request.getApplicableProductIds());
        existingCoupon.setRepetitionLimit(request.getRepetitionLimit());

        return couponRepository.save(existingCoupon);
    }

    //deleteCouponById
    public void deleteCouponById(Long id) {
        log.info("Deleting coupon with id: {}", id);

        if (!couponRepository.existsById(id)) {
            throw new CouponException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    //applicable-coupons
    public ApplicableCouponsResponse getApplicableCoupons(CartRequest cartRequest) {
        log.info("Finding applicable coupons for cart with {} items", cartRequest.getItems().size());

        LocalDateTime currentTime = LocalDateTime.now();
        //fetcching active coupons by currentTime
        List<Coupon> activeCoupons = couponRepository.findActiveAndValidCoupons(currentTime);

        List<CouponResponse> applicableCoupons = new ArrayList<>();

        for (Coupon coupon : activeCoupons) {
            try {
                CouponResponse response = convertToCouponResponse(coupon);
                BigDecimal discount = calculateDiscount(coupon, cartRequest);

                if (discount.compareTo(BigDecimal.ZERO) > 0) {
                    response.setCalculatedDiscount(discount);
                    response.setDiscountDescription(generateDiscountDescription(coupon, discount, cartRequest));
                    applicableCoupons.add(response);
                }
            } catch (Exception e) {
                log.warn("Error calculating discount for coupon {}: {}", coupon.getCode(), e.getMessage());
            }
        }

        // Sorting by highest discount amount,
        applicableCoupons.sort((a, b) -> b.getCalculatedDiscount().compareTo(a.getCalculatedDiscount()));

        return new ApplicableCouponsResponse(applicableCoupons, applicableCoupons.size());
    }

    //apply-coupon
    public Cart applyCoupon(Long couponId, CartRequest cartRequest) {
        log.info("Applying coupon {} to cart", couponId);

        Coupon coupon = getCouponById(couponId);

        // Validate coupon applicability
        validateCouponApplicability(coupon, cartRequest);

        // Create cart and apply discount
        Cart cart = createCartFromRequest(cartRequest);
        applyDiscountToCart(coupon, cart);

        // Increment coupon usage
        coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
        couponRepository.save(coupon);

        return cart;
    }

    private void validateCouponApplicability(Coupon coupon, CartRequest cartRequest) {
        LocalDateTime currentTime = LocalDateTime.now();

        if (!coupon.isActive()) {
            throw new CouponException("Coupon is not active");
        }

        if (coupon.getValidFrom() != null && currentTime.isBefore(coupon.getValidFrom())) {
            throw new CouponException("Coupon is not yet valid");
        }

        if (coupon.getValidUntil() != null && currentTime.isAfter(coupon.getValidUntil())) {
            throw new CouponException("Coupon has expired");
        }

        if (coupon.getMaxUsage() != null && coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            throw new CouponException("Coupon usage limit exceeded");
        }

        BigDecimal cartTotal = calculateCartTotal(cartRequest);
        if (coupon.getMinimumCartValue() != null &&
                cartTotal.compareTo(coupon.getMinimumCartValue()) < 0) {
            throw new CouponException("Cart total does not meet minimum requirement: " +
                    coupon.getMinimumCartValue());
        }

        BigDecimal discount = calculateDiscount(coupon, cartRequest);
        if (discount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CouponException("Coupon cannot be applied to this cart");
        }
    }

    private BigDecimal calculateDiscount(Coupon coupon, CartRequest cartRequest) {
        switch (coupon.getType()) {
            case CART_WISE:
                return calculateCartWiseDiscount(coupon, cartRequest);
            case PRODUCT_WISE:
                return calculateProductWiseDiscount(coupon, cartRequest);
            case BXGY:
                return calculateBxGyDiscount(coupon, cartRequest);
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateCartWiseDiscount(Coupon coupon, CartRequest cartRequest) {
        BigDecimal cartTotal = calculateCartTotal(cartRequest);

        if (coupon.getMinimumCartValue() != null &&
                cartTotal.compareTo(coupon.getMinimumCartValue()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = cartTotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getDiscountValue();
        }

        // Apply maximum discount limit
        if (coupon.getMaxDiscountAmount() != null) {
            discount = discount.min(coupon.getMaxDiscountAmount());
        }

        return discount.max(BigDecimal.ZERO);
    }

    private BigDecimal calculateProductWiseDiscount(Coupon coupon, CartRequest cartRequest) {
        if (coupon.getApplicableProductIds() == null || coupon.getApplicableProductIds().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartRequest.CartItemRequest item : cartRequest.getItems()) {
            if (coupon.getApplicableProductIds().contains(item.getProductId())) {
                BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal itemDiscount;

                if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                    itemDiscount = itemTotal.multiply(coupon.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else {
                    itemDiscount = coupon.getDiscountValue().multiply(BigDecimal.valueOf(item.getQuantity()));
                }

                totalDiscount = totalDiscount.add(itemDiscount);
            }
        }

        // Apply maximum discount limit
        if (coupon.getMaxDiscountAmount() != null) {
            totalDiscount = totalDiscount.min(coupon.getMaxDiscountAmount());
        }

        return totalDiscount.max(BigDecimal.ZERO);
    }

    private BigDecimal calculateBxGyDiscount(Coupon coupon, CartRequest cartRequest) {
        if (coupon.getBxgyRules() == null || coupon.getBxgyRules().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;
        int appliedCount = 0;

        // Sort rules by priority
        List<BxGyRule> sortedRules = coupon.getBxgyRules().stream()
                .sorted(Comparator.comparing(BxGyRule::getPriority))
                .collect(Collectors.toList());

        for (BxGyRule rule : sortedRules) {
            if (appliedCount >= coupon.getRepetitionLimit()) {
                break;
            }

            BigDecimal ruleDiscount = calculateBxGyRuleDiscount(rule, cartRequest);
            if (ruleDiscount.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(ruleDiscount);
                appliedCount++;
            }
        }

        return totalDiscount;
    }

    private BigDecimal calculateBxGyRuleDiscount(BxGyRule rule, CartRequest cartRequest) {
        // Count buy products
        int buyProductCount = 0;
        for (CartRequest.CartItemRequest item : cartRequest.getItems()) {
            if (rule.getBuyProductIds().contains(item.getProductId())) {
                buyProductCount += item.getQuantity();
            }
        }

        // Calculate number of times the rule can be applied
        int applicableTimes = buyProductCount / rule.getBuyQuantity();

        if (applicableTimes == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate total value of get products that would be free
        BigDecimal getProductValue = BigDecimal.ZERO;
        for (CartRequest.CartItemRequest item : cartRequest.getItems()) {
            if (rule.getGetProductIds().contains(item.getProductId())) {
                getProductValue = getProductValue.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        // Calculate discount based on applicable times and get product quantity
        int totalGetQuantity = applicableTimes * rule.getGetQuantity();
        BigDecimal averageGetProductPrice = getProductValue.divide(BigDecimal.valueOf(rule.getGetProductIds().size()), 2, RoundingMode.HALF_UP);

        return averageGetProductPrice.multiply(BigDecimal.valueOf(totalGetQuantity));
    }

    private BigDecimal calculateCartTotal(CartRequest cartRequest) {
        return cartRequest.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Cart createCartFromRequest(CartRequest cartRequest) {
        Cart cart = new Cart();
        List<CartItem> cartItems = new ArrayList<>();

        for (CartRequest.CartItemRequest itemRequest : cartRequest.getItems()) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductId(itemRequest.getProductId());
            cartItem.setProductName(itemRequest.getProductName());
            cartItem.setQuantity(itemRequest.getQuantity());
            cartItem.setPrice(itemRequest.getPrice());
            cartItem.setDiscount(BigDecimal.ZERO);
            cartItem.setTotalDiscount(BigDecimal.ZERO);
            cartItem.calculateFinalPrice();
            cartItems.add(cartItem);
        }
        cart.setItems(cartItems);
        cart.calculateTotals();
        return cart;
    }

    private void applyDiscountToCart(Coupon coupon, Cart cart) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        switch (coupon.getType()) {
            case CART_WISE:
                totalDiscount = applyCartWiseDiscount(coupon, cart);
                break;
            case PRODUCT_WISE:
                totalDiscount = applyProductWiseDiscount(coupon, cart);
                break;
            case BXGY:
                totalDiscount = applyBxGyDiscount(coupon, cart);
                break;
        }

        cart.setTotalDiscount(totalDiscount);
        cart.setAppliedCoupon(coupon);
        cart.calculateTotals();
    }

    private BigDecimal applyCartWiseDiscount(Coupon coupon, Cart cart) {
        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discount = cart.getTotalPrice().multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getDiscountValue();
        }

        if (coupon.getMaxDiscountAmount() != null) {
            discount = discount.min(coupon.getMaxDiscountAmount());
        }

        return discount.max(BigDecimal.ZERO);
    }

    private BigDecimal applyProductWiseDiscount(Coupon coupon, Cart cart) {
        if (coupon.getApplicableProductIds() == null || coupon.getApplicableProductIds().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            if (coupon.getApplicableProductIds().contains(item.getProductId())) {
                BigDecimal itemDiscount;

                if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                    itemDiscount = item.getPrice().multiply(coupon.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else {
                    itemDiscount = coupon.getDiscountValue();
                }

                item.setDiscount(itemDiscount);
                item.calculateFinalPrice();
                totalDiscount = totalDiscount.add(itemDiscount.multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        if (coupon.getMaxDiscountAmount() != null) {
            totalDiscount = totalDiscount.min(coupon.getMaxDiscountAmount());
        }

        return totalDiscount.max(BigDecimal.ZERO);
    }

    private BigDecimal applyBxGyDiscount(Coupon coupon, Cart cart) {
        if (coupon.getBxgyRules() == null || coupon.getBxgyRules().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;
        int appliedCount = 0;

        List<BxGyRule> sortedRules = coupon.getBxgyRules().stream()
                 .sorted(Comparator.comparing(BxGyRule::getPriority))
                 .collect(Collectors.toList());

        for (BxGyRule rule : sortedRules) {
            if (appliedCount >= coupon.getRepetitionLimit()) {
                break;
            }

            BigDecimal ruleDiscount = applyBxGyRuleDiscount(rule, cart);
            if (ruleDiscount.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(ruleDiscount);
                appliedCount++;
            }
        }

        return totalDiscount;
    }

    private BigDecimal applyBxGyRuleDiscount(BxGyRule rule, Cart cart) {

        int buyProductCount = 0;
        for (CartItem item : cart.getItems()) {
            if (rule.getBuyProductIds().contains(item.getProductId())) {
                buyProductCount += item.getQuantity();
            }
        }

        int applicableTimes = buyProductCount / rule.getBuyQuantity();

        if (applicableTimes == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal getProductValue = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            if (rule.getGetProductIds().contains(item.getProductId())) {
                getProductValue = getProductValue.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        int totalGetQuantity = applicableTimes * rule.getGetQuantity();
        BigDecimal averageGetProductPrice = getProductValue.divide(BigDecimal.valueOf(rule.getGetProductIds().size()), 2, RoundingMode.HALF_UP);

        return averageGetProductPrice.multiply(BigDecimal.valueOf(totalGetQuantity));
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

    private String generateDiscountDescription(Coupon coupon, BigDecimal discount, CartRequest cartRequest) {
        switch (coupon.getType()) {
            case CART_WISE:
                return String.format("%s off on cart total of %s",
                        coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE ?
                                coupon.getDiscountValue() + "%" : "$" + coupon.getDiscountValue(),
                        calculateCartTotal(cartRequest));
            case PRODUCT_WISE:
                return String.format("%s off on applicable products",
                        coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE ?
                                coupon.getDiscountValue() + "%" : "$" + coupon.getDiscountValue());
            case BXGY:
                return "Buy X Get Y discount applied";
            default:
                return "Discount applied";
        }
    }
} 