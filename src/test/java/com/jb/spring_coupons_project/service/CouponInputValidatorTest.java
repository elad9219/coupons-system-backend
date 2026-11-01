
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CouponInputValidatorTest {

    private CompanyService companyService;
    private CompanyRepository companyRepository;
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService();
        companyRepository = mock(CompanyRepository.class);
        couponRepository = mock(CouponRepository.class);

        ReflectionTestUtils.setField(
                companyService,
                "companyRepository",
                companyRepository
        );

        ReflectionTestUtils.setField(
                companyService,
                "couponRepository",
                couponRepository
        );
    }

    private Coupon validCoupon() {
        return Coupon.builder()
                .id(77)
                .companyId(5)
                .category(Category.FOOD)
                .title("Valid coupon")
                .description("Valid description")
                .start_date(LocalDate.of(2026, 10, 1))
                .end_date(LocalDate.of(2026, 12, 31))
                .amount(5)
                .price(149.99)
                .image("https://example.com/coupon.png")
                .build();
    }

    @Test
    void acceptsValidCouponAndTrimsText() throws Exception {
        Coupon coupon = validCoupon();
        coupon.setTitle("  Valid coupon  ");
        coupon.setDescription("  Valid description  ");

        CouponInputValidator.validate(coupon, true);

        assertEquals("Valid coupon", coupon.getTitle());
        assertEquals(
                "Valid description",
                coupon.getDescription()
        );
    }

    @Test
    void rejectsMissingCategory() {
        Coupon coupon = validCoupon();
        coupon.setCategory(null);

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );
    }

    @Test
    void rejectsBlankAndOversizedTitle() {
        Coupon coupon = validCoupon();
        coupon.setTitle(" ");

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );

        coupon.setTitle("A".repeat(26));

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );
    }

    @Test
    void rejectsBlankAndOversizedDescription() {
        Coupon coupon = validCoupon();
        coupon.setDescription("");

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );

        coupon.setDescription("A".repeat(1025));

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );
    }

    @Test
    void rejectsMissingOrReversedDates() {
        Coupon coupon = validCoupon();

        coupon.setStart_date(null);

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );

        coupon.setStart_date(LocalDate.of(2026, 12, 31));
        coupon.setEnd_date(LocalDate.of(2026, 10, 1));

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );
    }

    @Test
    void rejectsInvalidInitialStock() {
        Coupon coupon = validCoupon();
        coupon.setAmount(0);

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );

        coupon.setAmount(-1);

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );
    }

    @Test
    void acceptsZeroStockWhenUpdatingCoupon() throws Exception {
        Coupon coupon = validCoupon();
        coupon.setAmount(0);

        CouponInputValidator.validate(coupon, false);

        assertEquals(0, coupon.getAmount());
    }

    @Test
    void rejectsInvalidPrices() {
        Coupon coupon = validCoupon();

        for (double price : new double[]{
                -1, 0, 10000,
                Double.NaN,
                Double.POSITIVE_INFINITY
        }) {
            coupon.setPrice(price);

            assertThrows(
                    CouponException.class,
                    () -> CouponInputValidator.validate(
                            coupon, true
                    )
            );
        }
    }

    @Test
    void rejectsOversizedImageUrl() {
        Coupon coupon = validCoupon();
        coupon.setImage("x".repeat(1025));

        assertThrows(
                CouponException.class,
                () -> CouponInputValidator.validate(coupon, true)
        );
    }

    @Test
    void doesNotSaveInvalidNewCoupon() {
        when(companyRepository.existsById(5))
                .thenReturn(true);

        Coupon coupon = validCoupon();
        coupon.setPrice(-10);

        assertThrows(
                CouponException.class,
                () -> companyService.addCoupon(5, coupon)
        );

        verify(couponRepository, never())
                .save(any(Coupon.class));
    }

    @Test
    void newCouponCannotSetCompanyOrExpiredStatus()
            throws Exception {

        when(companyRepository.existsById(5))
                .thenReturn(true);

        Coupon coupon = validCoupon();
        coupon.setCompanyId(999);
        coupon.setExpired(true);

        companyService.addCoupon(5, coupon);

        assertEquals(5, coupon.getCompanyId());
        assertEquals(0, coupon.getId());
        assertFalse(coupon.isExpired());

        verify(couponRepository)
                .save(coupon);
    }

    @Test
    void invalidUpdateDoesNotModifyExistingCoupon() {
        Coupon existing = validCoupon();
        Coupon incoming = validCoupon();
        incoming.setPrice(-5);

        when(couponRepository.findById(77))
                .thenReturn(Optional.of(existing));

        assertThrows(
                CouponException.class,
                () -> companyService.updateCoupon(5, incoming)
        );

        assertEquals(149.99, existing.getPrice());
        assertEquals("Valid coupon", existing.getTitle());

        verify(couponRepository, never())
                .save(any(Coupon.class));
    }

    @Test
    void companyCannotUpdateAnotherCompanyCoupon() {
        Coupon existing = validCoupon();
        existing.setCompanyId(6);

        when(couponRepository.findById(77))
                .thenReturn(Optional.of(existing));

        assertThrows(
                CouponException.class,
                () -> companyService.updateCoupon(
                        5, validCoupon()
                )
        );

        verify(couponRepository, never())
                .save(any(Coupon.class));
    }
}
