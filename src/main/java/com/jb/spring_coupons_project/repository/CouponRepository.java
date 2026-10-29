
package com.jb.spring_coupons_project.repository;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends
        JpaRepository<Coupon, Integer>,
        JpaSpecificationExecutor<Coupon> {

    Optional<Coupon> findById(int id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :couponId")
    Optional<Coupon> findByIdForUpdate(
            @Param("couponId") int couponId
    );

    @Query(value = "SELECT * FROM coupons", nativeQuery = true)
    List<Coupon> getAllSystemCoupons();

    @Query(
            value = "SELECT * FROM coupons WHERE price <= ?1",
            nativeQuery = true
    )
    List<Coupon> getAllSystemCouponsByMaxPrice(double maxPrice);

    boolean existsByTitleAndCompanyId(String title, int id);

    @Query(
            value = "SELECT * FROM coupons WHERE company_id = ?1",
            nativeQuery = true
    )
    List<Coupon> getAllCompanyCoupons(int companyId);

    @Query(
            value = "SELECT * FROM coupons " +
                    "WHERE company_id = ?1 AND coupon_id = ?2",
            nativeQuery = true
    )
    List<Coupon> getOneCompanyCoupon(
            int companyId,
            int couponId
    );

    @Query(
            value = "SELECT COUNT(*) FROM customers_vs_coupons " +
                    "WHERE customer_id = ?1 AND coupon_id = ?2",
            nativeQuery = true
    )
    int checkCustomerSameCoupon(
            int customerId,
            int couponId
    );

    @Transactional
    @Modifying
    @Query(
            value = "INSERT INTO customers_vs_coupons " +
                    "(customer_id, coupon_id) VALUES (?1, ?2)",
            nativeQuery = true
    )
    void addPurchasedCoupon(int customerId, int couponId);

    @Query(
            value = "SELECT coupons.* FROM coupons " +
                    "INNER JOIN customers_vs_coupons " +
                    "ON coupons.coupon_id = customers_vs_coupons.coupon_id " +
                    "WHERE customers_vs_coupons.customer_id = ?1",
            nativeQuery = true
    )
    List<Coupon> findAllCustomerCoupons(int customerId);

    @Query(
            "SELECT coup FROM Coupon coup " +
                    "JOIN coup.customers cust " +
                    "WHERE cust.id = ?1 AND coup.price < ?2"
    )
    List<Coupon> findAllCustomerCouponsByMaxPrice(
            int customerId,
            double maxPrice
    );

    @Query(
            "SELECT c FROM Coupon c " +
                    "WHERE c.category = ?1 AND c.companyId = ?2"
    )
    List<Coupon> findAllCompanyCouponsByCategory(
            Category category,
            int companyId
    );

    @Query(
            "SELECT c FROM Coupon c " +
                    "WHERE c.price < ?1 AND c.companyId = ?2"
    )
    List<Coupon> findAllCompanyCouponsByMaxPrice(
            double maxPrice,
            int companyId
    );

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE coupons SET expired = true " +
                    "WHERE end_date < CURRENT_DATE " +
                    "AND expired = false",
            nativeQuery = true
    )
    void markExpiredCoupons();
}
