package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponPurchaseTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService();
        customerRepository = mock(CustomerRepository.class);
        couponRepository = mock(CouponRepository.class);

        ReflectionTestUtils.setField(
                customerService,
                "customerRepository",
                customerRepository
        );

        ReflectionTestUtils.setField(
                customerService,
                "couponRepository",
                couponRepository
        );
    }

    private Coupon availableCoupon(int amount) {
        Coupon coupon = new Coupon();
        coupon.setId(50);
        coupon.setAmount(amount);
        coupon.setExpired(false);
        coupon.setStart_date(LocalDate.now().minusDays(5));
        coupon.setEnd_date(LocalDate.now().plusDays(5));
        return coupon;
    }

    private void preparePurchase(
            int customerId,
            Coupon coupon) {

        when(customerRepository.existsById(customerId))
                .thenReturn(true);

        when(couponRepository.findByIdForUpdate(coupon.getId()))
                .thenReturn(Optional.of(coupon));
    }

    @Test
    void successfulPurchaseDecrementsStockAndAddsOwnership()
            throws Exception {

        Coupon coupon = availableCoupon(5);
        preparePurchase(101, coupon);

        customerService.purchaseCoupon(101, 50);

        assertEquals(4, coupon.getAmount());

        var order = inOrder(
                customerRepository,
                couponRepository
        );

        order.verify(customerRepository).existsById(101);
        order.verify(couponRepository).findByIdForUpdate(50);
        order.verify(couponRepository)
                .checkCustomerSameCoupon(101, 50);
        order.verify(couponRepository).saveAndFlush(coupon);
        order.verify(couponRepository)
                .addPurchasedCoupon(101, 50);
    }

    @Test
    void customerCannotPurchaseTheSameCouponTwice()
            throws Exception {

        Coupon coupon = availableCoupon(5);
        preparePurchase(101, coupon);

        when(couponRepository.checkCustomerSameCoupon(101, 50))
                .thenReturn(1);

        CouponException exception = assertThrows(
                CouponException.class,
                () -> customerService.purchaseCoupon(101, 50)
        );

        assertTrue(exception.getMessage().contains("already own"));
        assertEquals(5, coupon.getAmount());

        verify(couponRepository, never())
                .saveAndFlush(any(Coupon.class));

        verify(couponRepository, never())
                .addPurchasedCoupon(anyInt(), anyInt());
    }

    @Test
    void soldOutCouponCannotBePurchased()
            throws Exception {

        Coupon coupon = availableCoupon(0);
        preparePurchase(101, coupon);

        assertThrows(
                CouponException.class,
                () -> customerService.purchaseCoupon(101, 50)
        );

        assertEquals(0, coupon.getAmount());

        verify(couponRepository, never())
                .saveAndFlush(any(Coupon.class));

        verify(couponRepository, never())
                .addPurchasedCoupon(anyInt(), anyInt());
    }

    @Test
    void expiredCouponCannotBePurchased()
            throws Exception {

        Coupon coupon = availableCoupon(5);
        coupon.setEnd_date(LocalDate.now().minusDays(1));

        preparePurchase(101, coupon);

        assertThrows(
                CouponException.class,
                () -> customerService.purchaseCoupon(101, 50)
        );

        assertEquals(5, coupon.getAmount());

        verify(couponRepository, never())
                .addPurchasedCoupon(anyInt(), anyInt());
    }

    @Test
    void futureCouponCannotBePurchased()
            throws Exception {

        Coupon coupon = availableCoupon(5);
        coupon.setStart_date(LocalDate.now().plusDays(1));

        preparePurchase(101, coupon);

        assertThrows(
                CouponException.class,
                () -> customerService.purchaseCoupon(101, 50)
        );

        assertEquals(5, coupon.getAmount());

        verify(couponRepository, never())
                .addPurchasedCoupon(anyInt(), anyInt());
    }

    @Test
    void missingCustomerCannotPurchaseCoupon() {
        assertThrows(
                ExistsException.class,
                () -> customerService.purchaseCoupon(999, 50)
        );

        verifyNoInteractions(couponRepository);
    }

    @Test
    void missingCouponDoesNotChangeAnyStock()
            throws Exception {

        when(customerRepository.existsById(101))
                .thenReturn(true);

        assertThrows(
                ExistsException.class,
                () -> customerService.purchaseCoupon(101, 999)
        );

        verify(couponRepository).findByIdForUpdate(999);

        verify(couponRepository, never())
                .saveAndFlush(any(Coupon.class));

        verify(couponRepository, never())
                .addPurchasedCoupon(anyInt(), anyInt());
    }

    @Test
    void lastAvailableItemCannotBeSoldTwice()
            throws Exception {

        Coupon coupon = availableCoupon(1);

        when(customerRepository.existsById(101))
                .thenReturn(true);

        when(customerRepository.existsById(202))
                .thenReturn(true);

        when(couponRepository.findByIdForUpdate(50))
                .thenReturn(Optional.of(coupon));

        customerService.purchaseCoupon(101, 50);

        assertEquals(0, coupon.getAmount());

        assertThrows(
                CouponException.class,
                () -> customerService.purchaseCoupon(202, 50)
        );

        assertEquals(0, coupon.getAmount());

        verify(couponRepository, times(1))
                .saveAndFlush(coupon);

        verify(couponRepository, times(1))
                .addPurchasedCoupon(anyInt(), eq(50));
    }

    @Test
    void repositoryUsesPessimisticWriteLock()
            throws Exception {

        Method method = CouponRepository.class.getMethod(
                "findByIdForUpdate",
                int.class
        );

        Lock lock = method.getAnnotation(Lock.class);

        assertNotNull(lock);
        assertEquals(
                LockModeType.PESSIMISTIC_WRITE,
                lock.value()
        );
    }

    @Test
    void transactionRollsBackForCheckedExceptions()
            throws Exception {

        Method method = CustomerService.class.getMethod(
                "purchaseCoupon",
                int.class,
                int.class
        );

        Transactional annotation =
                method.getAnnotation(Transactional.class);

        assertNotNull(annotation);
        assertArrayEquals(
                new Class<?>[]{Exception.class},
                annotation.rollbackFor()
        );
    }
}