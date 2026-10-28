package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeedingTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CouponRepository couponRepository;

    @Test
    void purchasesAreInsertedAfterAllCouponSaves()
            throws Exception {

        when(companyRepository.count()).thenReturn(0L);

        AtomicInteger companySequence = new AtomicInteger(1);
        AtomicInteger customerSequence = new AtomicInteger(1);
        AtomicInteger couponSequence = new AtomicInteger(1);

        when(companyRepository.saveAll(any())).thenAnswer(invocation -> {
            Iterable<Company> companies = invocation.getArgument(0);
            List<Company> result = new ArrayList<>();

            for (Company company : companies) {
                company.setId(companySequence.getAndIncrement());
                result.add(company);
            }

            return result;
        });

        when(customerRepository.saveAll(any())).thenAnswer(invocation -> {
            Iterable<Customer> customers = invocation.getArgument(0);
            List<Customer> result = new ArrayList<>();

            for (Customer customer : customers) {
                customer.setId(customerSequence.getAndIncrement());
                result.add(customer);
            }

            return result;
        });

        when(couponRepository.saveAll(any())).thenAnswer(invocation -> {
            Iterable<Coupon> coupons = invocation.getArgument(0);
            List<Coupon> result = new ArrayList<>();

            for (Coupon coupon : coupons) {
                if (coupon.getId() == 0) {
                    coupon.setId(couponSequence.getAndIncrement());
                }

                result.add(coupon);
            }

            return result;
        });

        DataSeeding seeding = new DataSeeding(
                companyRepository,
                customerRepository,
                couponRepository
        );

        seeding.run();

        InOrder order = inOrder(couponRepository);

        order.verify(couponRepository, times(2))
                .saveAll(any());

        order.verify(couponRepository)
                .addPurchasedCoupon(1, 1);

        order.verify(couponRepository)
                .addPurchasedCoupon(1, 2);

        order.verifyNoMoreInteractions();

        verify(companyRepository).saveAll(
                argThat(companies -> count(companies) == 15)
        );

        verify(customerRepository).saveAll(
                argThat(customers -> count(customers) == 21)
        );

        assertEquals(51, couponSequence.get());
    }

    @Test
    void existingDemoDataIsNotSeededAgain()
            throws Exception {

        when(companyRepository.count()).thenReturn(1L);

        DataSeeding seeding = new DataSeeding(
                companyRepository,
                customerRepository,
                couponRepository
        );

        seeding.run();

        verify(companyRepository).count();
        verifyNoMoreInteractions(companyRepository);
        verifyNoInteractions(
                customerRepository,
                couponRepository
        );
    }

    private static int count(Iterable<?> values) {
        int count = 0;

        for (Object ignored : values) {
            count++;
        }

        return count;
    }
}