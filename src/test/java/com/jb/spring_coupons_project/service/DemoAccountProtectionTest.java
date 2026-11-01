
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DemoAccountProtectionTest {

    private AdminService adminService;
    private CompanyService companyService;
    private CustomerService customerService;

    private CompanyRepository companyRepository;
    private CustomerRepository customerRepository;
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        adminService = new AdminService();
        companyService = new CompanyService();
        customerService = new CustomerService();

        companyRepository = mock(CompanyRepository.class);
        customerRepository = mock(CustomerRepository.class);
        couponRepository = mock(CouponRepository.class);

        for (ClientService service : List.of(
                adminService,
                companyService,
                customerService
        )) {
            ReflectionTestUtils.setField(
                    service, "companyRepository", companyRepository
            );
            ReflectionTestUtils.setField(
                    service, "customerRepository", customerRepository
            );
            ReflectionTestUtils.setField(
                    service, "couponRepository", couponRepository
            );
            ReflectionTestUtils.setField(
                    service, "publicDemo", true
            );
        }
    }

    private Company demoCompany() {
        return Company.builder()
                .id(10)
                .name("Sony")
                .email("sony@contact.com")
                .password("existing-password-hash")
                .build();
    }

    private Customer demoCustomer() {
        return Customer.builder()
                .id(20)
                .first_name("Kobi")
                .last_name("Shasha")
                .email("kobi@gmail.com")
                .password("existing-password-hash")
                .build();
    }

    private Coupon validCoupon() {
        return Coupon.builder()
                .id(30)
                .companyId(10)
                .category(Category.FOOD)
                .title("Test coupon")
                .description("A valid coupon for testing")
                .start_date(LocalDate.now().minusDays(5))
                .end_date(LocalDate.now().plusDays(15))
                .amount(5)
                .price(100)
                .image("https://example.com/coupon.png")
                .build();
    }

    @Test
    void adminCannotUpdateDemoCompany() {
        when(companyRepository.findById(10))
                .thenReturn(Optional.of(demoCompany()));

        Company incoming = new Company();
        incoming.setId(10);
        incoming.setEmail("changed@example.com");

        assertThrows(
                CompanyException.class,
                () -> adminService.updateCompany(incoming)
        );

        verify(companyRepository, never())
                .saveAndFlush(any(Company.class));
    }

    @Test
    void adminCannotDeleteDemoCompany() {
        when(companyRepository.findById(10))
                .thenReturn(Optional.of(demoCompany()));

        assertThrows(
                CompanyException.class,
                () -> adminService.deleteCompany(10)
        );

        verify(companyRepository, never())
                .deleteById(anyInt());
    }

    @Test
    void adminCannotUpdateDemoCustomer() {
        when(customerRepository.findById(20))
                .thenReturn(Optional.of(demoCustomer()));

        Customer incoming = new Customer();
        incoming.setId(20);
        incoming.setEmail("changed@example.com");

        assertThrows(
                CustomerException.class,
                () -> adminService.updateCustomer(incoming)
        );

        verify(customerRepository, never())
                .saveAndFlush(any(Customer.class));
    }

    @Test
    void adminCannotDeleteDemoCustomer() {
        when(customerRepository.findById(20))
                .thenReturn(Optional.of(demoCustomer()));

        assertThrows(
                CustomerException.class,
                () -> adminService.deleteCustomer(20)
        );

        verify(customerRepository, never())
                .deletePurchaseLinksByCustomerId(anyInt());

        verify(customerRepository, never())
                .deleteCustomerRowById(anyInt());
    }

    @Test
    void companyCannotModifyOwnDemoAccount() {
        when(companyRepository.findById(10))
                .thenReturn(Optional.of(demoCompany()));

        Company incoming = new Company();
        incoming.setEmail("changed@example.com");

        assertThrows(
                CompanyException.class,
                () -> companyService.updateCompanyDetails(10, incoming)
        );

        verify(companyRepository, never())
                .saveAndFlush(any(Company.class));
    }

    @Test
    void customerCannotModifyOwnDemoAccount() {
        when(customerRepository.findById(20))
                .thenReturn(Optional.of(demoCustomer()));

        Customer incoming = new Customer();
        incoming.setEmail("changed@example.com");

        assertThrows(
                CustomerException.class,
                () -> customerService.updateCustomerDetails(20, incoming)
        );

        verify(customerRepository, never())
                .saveAndFlush(any(Customer.class));
    }

    @Test
    void regularCompanyCanUpdateDetailsInPublicDemo()
            throws Exception {

        Company existing = Company.builder()
                .id(11)
                .name("Another company")
                .email("other@example.com")
                .build();

        when(companyRepository.findById(11))
                .thenReturn(Optional.of(existing));

        Company incoming = new Company();
        incoming.setEmail("other@example.com");

        companyService.updateCompanyDetails(11, incoming);

        verify(companyRepository).saveAndFlush(existing);
    }

    @Test
    void regularCustomerCanUpdateDetailsInPublicDemo()
            throws Exception {

        Customer existing = Customer.builder()
                .id(21)
                .first_name("Original")
                .last_name("Customer")
                .email("other@example.com")
                .build();

        when(customerRepository.findById(21))
                .thenReturn(Optional.of(existing));

        Customer incoming = new Customer();
        incoming.setFirst_name("Updated");
        incoming.setLast_name("Customer");
        incoming.setEmail("other@example.com");

        customerService.updateCustomerDetails(21, incoming);

        assertEquals("Updated", existing.getFirst_name());
        verify(customerRepository).saveAndFlush(existing);
    }

    @Test
    void demoCompanyCanStillCreateCoupons()
            throws Exception {

        when(companyRepository.existsById(10))
                .thenReturn(true);

        Coupon coupon = validCoupon();
        coupon.setId(0);

        companyService.addCoupon(10, coupon);

        assertEquals(10, coupon.getCompanyId());
        assertFalse(coupon.isExpired());

        verify(couponRepository).save(coupon);
    }

    @Test
    void demoCustomerCanStillPurchaseCoupons()
            throws Exception {

        Coupon coupon = validCoupon();
        coupon.setAmount(2);

        when(customerRepository.existsById(20))
                .thenReturn(true);

        when(couponRepository.findByIdForUpdate(30))
                .thenReturn(Optional.of(coupon));

        customerService.purchaseCoupon(20, 30);

        assertEquals(1, coupon.getAmount());

        verify(couponRepository).saveAndFlush(coupon);
        verify(couponRepository).addPurchasedCoupon(20, 30);
    }

    @Test
    void updatingExpirationToFutureReactivatesCoupon()
            throws Exception {

        Coupon existing = validCoupon();
        existing.setExpired(true);

        Coupon incoming = validCoupon();
        incoming.setEnd_date(LocalDate.now().plusDays(30));

        when(couponRepository.findById(30))
                .thenReturn(Optional.of(existing));

        companyService.updateCoupon(10, incoming);

        assertFalse(existing.isExpired());
        verify(couponRepository).save(existing);
    }

    @Test
    void updatingExpirationToPastMarksCouponExpired()
            throws Exception {

        Coupon existing = validCoupon();

        Coupon incoming = validCoupon();
        incoming.setStart_date(LocalDate.now().minusDays(20));
        incoming.setEnd_date(LocalDate.now().minusDays(1));

        when(couponRepository.findById(30))
                .thenReturn(Optional.of(existing));

        companyService.updateCoupon(10, incoming);

        assertTrue(existing.isExpired());
        verify(couponRepository).save(existing);
    }

    @Test
    void demoAccountCanBeEditedOutsidePublicDemo()
            throws Exception {

        ReflectionTestUtils.setField(
                companyService,
                "publicDemo",
                false
        );

        Company existing = demoCompany();

        when(companyRepository.findById(10))
                .thenReturn(Optional.of(existing));

        Company incoming = new Company();
        incoming.setEmail("sony@contact.com");

        companyService.updateCompanyDetails(10, incoming);

        verify(companyRepository).saveAndFlush(existing);
    }
}
