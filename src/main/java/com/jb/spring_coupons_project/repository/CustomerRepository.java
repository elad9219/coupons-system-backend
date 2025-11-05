package com.jb.spring_coupons_project.repository;

import com.jb.spring_coupons_project.beans.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {


    boolean existsByEmail(String email);

    Optional <Customer> findByEmailAndPassword(String email, String password);

    boolean existsById(int id);


    @Query(value = "DELETE FROM customers_vs_coupons WHERE coupon_id = ?", nativeQuery = true)
    @Modifying
    void deleteCouponsByCouponId(int couponId);
}