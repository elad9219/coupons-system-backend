package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.security.PasswordCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeedingTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CouponRepository couponRepository;

    @Captor
    private ArgumentCaptor<List<Company>> companiesCaptor;
    @Captor
    private ArgumentCaptor<List<Customer>> customersCaptor;
    @Captor
    private ArgumentCaptor<List<Coupon>> couponsCaptor;

    @Test
    void curatedCatalogPreservesDemoLoginInventoryAndPurchaseOrder() {
        AtomicInteger companySequence = new AtomicInteger(101);
        AtomicInteger customerSequence = new AtomicInteger(501);
        AtomicInteger couponSequence = new AtomicInteger(1001);
        Map<Integer, Integer> initialStock = new HashMap<>();
        LocalDate before = LocalDate.now();

        when(companyRepository.saveAll(any())).thenAnswer(invocation -> {
            List<Company> companies = invocation.getArgument(0);
            companies.forEach(company -> company.setId(companySequence.getAndIncrement()));
            return companies;
        });
        when(customerRepository.saveAll(any())).thenAnswer(invocation -> {
            List<Customer> customers = invocation.getArgument(0);
            customers.forEach(customer -> customer.setId(customerSequence.getAndIncrement()));
            return customers;
        });
        when(couponRepository.saveAll(any())).thenAnswer(invocation -> {
            List<Coupon> coupons = invocation.getArgument(0);
            coupons.forEach(coupon -> {
                if (coupon.getId() == 0) {
                    coupon.setId(couponSequence.getAndIncrement());
                    initialStock.put(coupon.getId(), coupon.getAmount());
                }
            });
            return coupons;
        });

        seeding().run();
        LocalDate after = LocalDate.now();

        verify(companyRepository).saveAll(companiesCaptor.capture());
        verify(customerRepository).saveAll(customersCaptor.capture());
        verify(couponRepository, times(2)).saveAll(couponsCaptor.capture());
        List<Company> companies = companiesCaptor.getValue();
        List<Customer> customers = customersCaptor.getValue();
        List<Coupon> coupons = couponsCaptor.getAllValues().get(0);

        assertEquals(15, companies.size());
        assertEquals(21, customers.size());
        assertEquals(50, coupons.size());
        assertEquals(15L, companies.stream().map(Company::getEmail).distinct().count());
        assertEquals(21L, customers.stream().map(Customer::getEmail).distinct().count());
        assertEquals(50L, coupons.stream().map(Coupon::getTitle).distinct().count());
        assertEquals("Sony", companies.get(0).getName());
        assertEquals("sony@contact.com", companies.get(0).getEmail());
        assertEquals("Kobi", customers.get(0).getFirst_name());
        assertEquals("Shasha", customers.get(0).getLast_name());
        assertEquals("kobi@gmail.com", customers.get(0).getEmail());
        assertTrue(companies.stream().allMatch(c -> PasswordCodec.isEncoded(c.getPassword())));
        assertTrue(customers.stream().allMatch(c -> PasswordCodec.isEncoded(c.getPassword())));
        assertTrue(PasswordCodec.matches("1234", companies.get(0).getPassword()));
        assertTrue(PasswordCodec.matches("1234", customers.get(0).getPassword()));

        Set<Integer> companyIds = companies.stream().map(Company::getId).collect(Collectors.toSet());
        assertEquals(companyIds, coupons.stream().map(Coupon::getCompanyId).collect(Collectors.toSet()));
        assertEquals(EnumSet.allOf(Category.class),
                coupons.stream().map(Coupon::getCategory).collect(Collectors.toSet()));
        assertEquals(Set.of(Category.ELECTRICITY), coupons.stream()
                .filter(c -> c.getCompanyId() == companies.get(0).getId())
                .map(Coupon::getCategory).collect(Collectors.toSet()));
        assertTrue(coupons.stream().map(Coupon::getImage).distinct().count() >= 30);
        assertEquals(2L, coupons.stream().filter(c -> c.getAmount() == 0).count());
        assertEquals(2L, coupons.stream().filter(c -> c.getStart_date().isAfter(after)).count());

        for (Coupon coupon : coupons) {
            assertFalse(coupon.getTitle().isBlank());
            assertTrue(coupon.getTitle().length() <= 25);
            assertTrue(coupon.getDescription().length() <= 1024);
            assertTrue(coupon.getImage().startsWith("https://"));
            assertTrue(coupon.getImage().length() <= 1024);
            assertTrue(coupon.getPrice() > 0 && coupon.getPrice() <= 10000);
            assertTrue(coupon.getEnd_date().isAfter(after));
            assertTrue(coupon.getEnd_date().isAfter(coupon.getStart_date()));
            assertFalse(coupon.isExpired());
            int purchased = coupon.getId() == 1001 || coupon.getId() == 1002 ? 1 : 0;
            assertEquals(initialStock.get(coupon.getId()) - purchased, coupon.getAmount());
        }
        assertFalse(coupons.get(0).getStart_date().isAfter(before));
        assertFalse(coupons.get(1).getStart_date().isAfter(before));

        InOrder order = inOrder(couponRepository);
        order.verify(couponRepository).count();
        order.verify(couponRepository).saveAll(coupons);
        order.verify(couponRepository).saveAll(couponsCaptor.getAllValues().get(1));
        order.verify(couponRepository).addPurchasedCoupon(501, 1001);
        order.verify(couponRepository).addPurchasedCoupon(501, 1002);
        order.verifyNoMoreInteractions();
    }

    @Test
    void existingDemoDataIsNotSeededAgain() {
        when(companyRepository.count()).thenReturn(1L);
        seeding().run();
        verify(companyRepository).count();
        verifyNoMoreInteractions(companyRepository);
        verifyNoInteractions(customerRepository, couponRepository);
    }

    @Test
    void existingCustomersPreventSeedingAnOtherwiseEmptyDatabase() {
        when(customerRepository.count()).thenReturn(1L);
        seeding().run();
        verify(companyRepository).count();
        verify(customerRepository).count();
        verifyNoMoreInteractions(companyRepository, customerRepository);
        verifyNoInteractions(couponRepository);
    }

    @Test
    void existingCouponsPreventSeedingAnOtherwiseEmptyDatabase() {
        when(couponRepository.count()).thenReturn(1L);
        seeding().run();
        verify(companyRepository).count();
        verify(customerRepository).count();
        verify(couponRepository).count();
        verifyNoMoreInteractions(companyRepository, customerRepository, couponRepository);
    }

    private DataSeeding seeding() {
        return new DataSeeding(companyRepository, customerRepository, couponRepository);
    }
}