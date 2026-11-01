
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:coupon_deletion_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "COUPONS_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
class CouponDeletionTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void deletingPurchasedCouponPreservesCustomerAndOtherPurchases()
            throws Exception {

        Customer customer = customerRepository
                .findByEmail("kobi@gmail.com")
                .orElseThrow();

        List<Coupon> purchases =
                couponRepository.findAllCustomerCoupons(
                        customer.getId()
                );

        assertTrue(purchases.size() >= 2);

        Coupon deletedCoupon = purchases.get(0);
        Coupon remainingCoupon = purchases.get(1);

        int customerId = customer.getId();
        int deletedCouponId = deletedCoupon.getId();
        int remainingCouponId = remainingCoupon.getId();

        companyService.deleteCoupon(
                deletedCoupon.getCompanyId(),
                deletedCouponId
        );

        entityManager.flush();
        entityManager.clear();

        assertEquals(
                0,
                count(
                        "SELECT COUNT(*) FROM coupons WHERE coupon_id = ?",
                        deletedCouponId
                )
        );

        assertEquals(
                0,
                count(
                        "SELECT COUNT(*) FROM customers_vs_coupons WHERE coupon_id = ?",
                        deletedCouponId
                )
        );

        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM customers WHERE customer_id = ?",
                        customerId
                )
        );

        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM coupons WHERE coupon_id = ?",
                        remainingCouponId
                )
        );

        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM customers_vs_coupons " +
                                "WHERE customer_id = ? AND coupon_id = ?",
                        customerId,
                        remainingCouponId
                )
        );
    }

    @Test
    @Transactional
    void anotherCompanyCannotDeleteCoupon() {

        Coupon coupon = couponRepository.findAll()
                .getFirst();

        int couponId = coupon.getId();

        assertThrows(
                ExistsException.class,
                () -> companyService.deleteCoupon(
                        Integer.MAX_VALUE,
                        couponId
                )
        );

        assertTrue(
                couponRepository.existsById(couponId)
        );
    }

    @Test
    @Transactional
    void deletingMissingCouponDoesNotModifyData() {

        long couponCount = couponRepository.count();
        long customerCount = customerRepository.count();

        assertThrows(
                ExistsException.class,
                () -> companyService.deleteCoupon(
                        1,
                        Integer.MAX_VALUE
                )
        );

        assertEquals(
                couponCount,
                couponRepository.count()
        );

        assertEquals(
                customerCount,
                customerRepository.count()
        );
    }

    private int count(String sql, Object... parameters) {
        Integer result = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                parameters
        );

        assertNotNull(result);
        return result;
    }
}
