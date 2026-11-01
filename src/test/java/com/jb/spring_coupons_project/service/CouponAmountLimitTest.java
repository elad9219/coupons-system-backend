
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.exception.CouponException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CouponAmountLimitTest {

    private Coupon couponWithAmount(int amount) {
        return Coupon.builder()
                .category(Category.FOOD)
                .title("Test coupon")
                .description("Test description")
                .start_date(LocalDate.of(2030, 1, 1))
                .end_date(LocalDate.of(2030, 1, 31))
                .amount(amount)
                .price(100)
                .build();
    }

    @Test
    void acceptsMaximumAmount() {
        assertDoesNotThrow(() ->
                CouponInputValidator.validate(
                        couponWithAmount(100_000),
                        true
                )
        );
    }

    @Test
    void rejectsExcessiveAmountWhenCreating() {
        CouponException exception = assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(
                        couponWithAmount(100_001),
                        true
                )
        );

        assertTrue(exception.getMessage().contains("100000"));
    }

    @Test
    void rejectsExcessiveAmountWhenUpdating() {
        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(
                        couponWithAmount(Integer.MAX_VALUE),
                        false
                )
        );
    }

    @Test
    void preservesZeroStockForUpdatesOnly() {
        assertDoesNotThrow(() ->
                CouponInputValidator.validate(
                        couponWithAmount(0),
                        false
                )
        );

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(
                        couponWithAmount(0),
                        true
                )
        );
    }
}
