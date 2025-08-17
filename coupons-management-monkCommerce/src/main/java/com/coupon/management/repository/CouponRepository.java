package com.coupon.management.repository;


import com.coupon.management.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    List<Coupon> findByActiveTrue();

    @Query("SELECT c FROM Coupon c WHERE c.active = true AND " +
            "(c.validFrom IS NULL OR c.validFrom <= :currentTime) AND " +
            "(c.validUntil IS NULL OR c.validUntil >= :currentTime) AND " +
            "(c.maxUsage IS NULL OR c.currentUsage < c.maxUsage)")
    List<Coupon> findActiveAndValidCoupons(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.type = :type AND " +
            "(c.validFrom IS NULL OR c.validFrom <= :currentTime) AND " +
            "(c.validUntil IS NULL OR c.validUntil >= :currentTime) AND " +
            "(c.maxUsage IS NULL OR c.currentUsage < c.maxUsage)")
    List<Coupon> findActiveAndValidCouponsByType(@Param("type") Coupon.CouponType type,
                                                 @Param("currentTime") LocalDateTime currentTime);

    boolean existsByCode(String code);
} 