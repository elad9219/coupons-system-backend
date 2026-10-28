package com.jb.spring_coupons_project.repository;

import com.jb.spring_coupons_project.beans.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByEmailAndPassword(
            String email,
            String password
    );

    boolean existsById(int id);

    boolean existsByIdAndEmail(int id, String email);

    @Query("SELECT c.email FROM Customer c WHERE c.id = ?1")
    Optional<String> findEmailForAuthById(int id);

    @Modifying
    @Query(
            value = "DELETE FROM customers_vs_coupons " +
                    "WHERE coupon_id = ?1",
            nativeQuery = true
    )
    void deleteCouponsByCouponId(int couponId);

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query(
            value = "DELETE FROM customers_vs_coupons " +
                    "WHERE customer_id = :customerId",
            nativeQuery = true
    )
    int deletePurchaseLinksByCustomerId(
            @Param("customerId") int customerId
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query(
            value = "DELETE FROM customers " +
                    "WHERE customer_id = :customerId",
            nativeQuery = true
    )
    int deleteCustomerRowById(
            @Param("customerId") int customerId
    );
}