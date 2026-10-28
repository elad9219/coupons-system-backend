package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.UserType;
import com.jb.spring_coupons_project.controller.CompanyController;
import com.jb.spring_coupons_project.controller.CustomerController;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.security.JWTutil;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestIsolationTest {

    @Test
    void customerControllerUsesIdentityFromCurrentToken()
            throws Exception {

        JWTutil jwtUtil = mock(JWTutil.class);
        CustomerService customerService = mock(CustomerService.class);

        when(jwtUtil.checkUser("customer-token", UserType.CUSTOMER))
                .thenReturn(101);

        when(jwtUtil.generateToken("customer-token"))
                .thenReturn("Bearer refreshed-token");

        CustomerController controller = new CustomerController(
                jwtUtil,
                customerService
        );

        controller.purchaseCoupon("customer-token", 50);

        verify(customerService).purchaseCoupon(101, 50);
        verifyNoMoreInteractions(customerService);
    }

    @Test
    void companyControllerUsesIdentityFromCurrentToken()
            throws Exception {

        JWTutil jwtUtil = mock(JWTutil.class);
        CompanyService companyService = mock(CompanyService.class);

        when(jwtUtil.checkUser("company-token", UserType.COMPANY))
                .thenReturn(202);

        when(jwtUtil.generateToken("company-token"))
                .thenReturn("Bearer refreshed-token");

        CompanyController controller = new CompanyController(
                companyService,
                jwtUtil
        );

        Coupon coupon = new Coupon();
        coupon.setTitle("Example");

        controller.addCoupon("company-token", coupon);

        verify(companyService).addCoupon(202, coupon);
        verifyNoMoreInteractions(companyService);
    }

    @Test
    void customerServiceUsesExplicitIdentityForEveryRead()
            throws Exception {

        CouponRepository couponRepository =
                mock(CouponRepository.class);

        CustomerService service = new CustomerService();

        ReflectionTestUtils.setField(
                service,
                "couponRepository",
                couponRepository
        );

        Coupon firstCoupon = new Coupon();
        firstCoupon.setId(11);

        Coupon secondCoupon = new Coupon();
        secondCoupon.setId(22);

        when(couponRepository.findAllCustomerCoupons(101))
                .thenReturn(List.of(firstCoupon));

        when(couponRepository.findAllCustomerCoupons(202))
                .thenReturn(List.of(secondCoupon));

        assertEquals(
                11,
                service.getCustomerCoupons(101).get(0).getId()
        );

        assertEquals(
                22,
                service.getCustomerCoupons(202).get(0).getId()
        );

        assertEquals(
                11,
                service.getCustomerCoupons(101).get(0).getId()
        );
    }

    @Test
    void companyServiceUsesExplicitIdentityForEveryRead()
            throws Exception {

        CouponRepository couponRepository =
                mock(CouponRepository.class);

        CompanyService service = new CompanyService();

        ReflectionTestUtils.setField(
                service,
                "couponRepository",
                couponRepository
        );

        Coupon firstCoupon = new Coupon();
        firstCoupon.setId(31);

        Coupon secondCoupon = new Coupon();
        secondCoupon.setId(42);

        when(couponRepository.getAllCompanyCoupons(101))
                .thenReturn(List.of(firstCoupon));

        when(couponRepository.getAllCompanyCoupons(202))
                .thenReturn(List.of(secondCoupon));

        assertEquals(
                31,
                service.getAllCompanyCoupons(101).get(0).getId()
        );

        assertEquals(
                42,
                service.getAllCompanyCoupons(202).get(0).getId()
        );

        assertEquals(
                31,
                service.getAllCompanyCoupons(101).get(0).getId()
        );
    }

    @Test
    void companyCannotUpdateAnotherCompanyCoupon() {
        CouponRepository couponRepository =
                mock(CouponRepository.class);

        CompanyService service = new CompanyService();

        ReflectionTestUtils.setField(
                service,
                "couponRepository",
                couponRepository
        );

        Coupon existing = new Coupon();
        existing.setId(50);
        existing.setCompanyId(202);
        existing.setTitle("Another Company Coupon");

        Coupon incoming = new Coupon();
        incoming.setId(50);
        incoming.setTitle("Modified Coupon");

        when(couponRepository.findById(50))
                .thenReturn(Optional.of(existing));

        assertThrows(
                CouponException.class,
                () -> service.updateCoupon(101, incoming)
        );

        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void newCouponCannotChooseAnotherCompanyId()
            throws Exception {

        CouponRepository couponRepository =
                mock(CouponRepository.class);

        CompanyRepository companyRepository =
                mock(CompanyRepository.class);

        CompanyService service = new CompanyService();

        ReflectionTestUtils.setField(
                service,
                "couponRepository",
                couponRepository
        );

        ReflectionTestUtils.setField(
                service,
                "companyRepository",
                companyRepository
        );

        when(companyRepository.existsById(101))
                .thenReturn(true);

        Coupon coupon = new Coupon();
        coupon.setId(999);
        coupon.setCompanyId(202);
        coupon.setTitle("New Coupon");

        service.addCoupon(101, coupon);

        assertEquals(0, coupon.getId());
        assertEquals(101, coupon.getCompanyId());

        verify(couponRepository).save(coupon);
    }
}