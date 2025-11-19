package com.jb.spring_coupons_project.repository;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    Optional<Coupon> findById(int id);

    @Query(value = "SELECT * FROM coupons", nativeQuery = true)
    List<Coupon> getAllSystemCoupons();

    @Query(value = "SELECT * FROM coupons WHERE price <=?", nativeQuery = true)
    List<Coupon> getAllSystemCouponsByMaxPrice(double maxPrice);

    boolean existsByTitleAndCompanyId(String title, int id);

    @Query(value = "SELECT * FROM coupons WHERE company_id = ?1", nativeQuery = true)
    List<Coupon> getAllCompanyCoupons(int company_id);

    @Query(value = "SELECT * FROM coupons WHERE company_id = ?1 AND coupon_id = ?2", nativeQuery = true)
    List<Coupon> getOneCompanyCoupon(int company_id, int coupon_id);

    @Query(value = "SELECT COUNT(*) FROM customers_vs_coupons WHERE customer_id = ?1 " +
            "AND coupon_id = ?2", nativeQuery = true)
    int checkCustomerSameCoupon(int customer_id, int coupon_id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO customers_vs_coupons (customer_id, coupon_id) " +
            "VALUES (?1,?2)", nativeQuery = true)
    void addPurchasedCoupon(int customer_id, int coupon_id);

    @Query(value = "SELECT * FROM coupons INNER JOIN customers_vs_coupons " +
            "ON coupons.coupon_id = customers_vs_coupons.coupon_id " +
            "WHERE customers_vs_coupons.customer_id = ?", nativeQuery = true)
    List<Coupon> findAllCustomerCoupons(int customer_id);

    @Query(value = "SELECT coup FROM Coupon coup join coup.customers cust where cust.id =?1 and coup.price < ?2")
    List<Coupon> findAllCustomerCouponsByMaxPrice(int customer_id, double maxPrice);

    @Query(value = "SELECT c FROM Coupon c WHERE category = ?1 AND company_id = ?2")
    List<Coupon> findAllCompanyCouponsByCategory(Category category, int company_id);

    @Query(value = "SELECT c FROM Coupon c WHERE price < ?1 AND company_id = ?2")
    List<Coupon> findAllCompanyCouponsByMaxPrice(double maxPrice, int company_id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE coupons SET expired = true WHERE end_date < CURRENT_DATE AND expired = false", nativeQuery = true)
    void markExpiredCoupons();
}