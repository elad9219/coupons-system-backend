package com.jb.spring_coupons_project.security;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.beans.UserDetails;
import com.jb.spring_coupons_project.beans.UserType;
import com.jb.spring_coupons_project.controller.AdminController;
import com.jb.spring_coupons_project.exception.TokenException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.LoginException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtSecurityTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString(
                    "0123456789abcdef0123456789abcdef"
                            .getBytes(StandardCharsets.UTF_8)
            );

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AdminService adminService;

    private JWTutil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTutil(
                SECRET,
                companyRepository,
                customerRepository
        );
    }

    @Test
    void differentCustomersKeepSeparateIdentities()
            throws Exception {

        Customer first = new Customer();
        first.setId(101);
        first.setEmail("first@example.com");

        Customer second = new Customer();
        second.setId(202);
        second.setEmail("second@example.com");

        when(customerRepository.findByEmail(first.getEmail()))
                .thenReturn(Optional.of(first));

        when(customerRepository.findByEmail(second.getEmail()))
                .thenReturn(Optional.of(second));

        when(customerRepository.existsByIdAndEmail(
                101, first.getEmail())).thenReturn(true);

        when(customerRepository.existsByIdAndEmail(
                202, second.getEmail())).thenReturn(true);

        String firstToken = jwtUtil.generateToken(
                new UserDetails(
                        first.getEmail(),
                        "password",
                        UserType.CUSTOMER
                )
        );

        String secondToken = jwtUtil.generateToken(
                new UserDetails(
                        second.getEmail(),
                        "password",
                        UserType.CUSTOMER
                )
        );

        assertEquals(
                101,
                jwtUtil.checkUser(firstToken, UserType.CUSTOMER)
        );

        assertEquals(
                202,
                jwtUtil.checkUser(secondToken, UserType.CUSTOMER)
        );

        assertEquals(
                101,
                jwtUtil.checkUser(firstToken, UserType.CUSTOMER)
        );
    }

    @Test
    void companyTokenCannotAccessAdminRole() {
        Company company = new Company();
        company.setId(55);
        company.setEmail("company@example.com");

        when(companyRepository.findByEmail(company.getEmail()))
                .thenReturn(company);

        String token = jwtUtil.generateToken(
                new UserDetails(
                        company.getEmail(),
                        "password",
                        UserType.COMPANY
                )
        );

        assertThrows(
                LoginException.class,
                () -> jwtUtil.checkUser(token, UserType.ADMIN)
        );
    }

    @Test
    void customerCannotCreateCompanyThroughAdminController()
            throws Exception {

        Customer customer = new Customer();
        customer.setId(33);
        customer.setEmail("customer@example.com");

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        String token = jwtUtil.generateToken(
                new UserDetails(
                        customer.getEmail(),
                        "password",
                        UserType.CUSTOMER
                )
        );

        AdminController controller = new AdminController(
                adminService,
                companyRepository,
                customerRepository,
                jwtUtil
        );

        Company newCompany = new Company();
        newCompany.setName("Test Company");

        assertThrows(
                LoginException.class,
                () -> controller.addCompany(token, newCompany)
        );

        verifyNoInteractions(adminService);
    }

    @Test
    void deletedOrReplacedAccountCannotReuseOldIdentity() {
        Customer customer = new Customer();
        customer.setId(44);
        customer.setEmail("original@example.com");

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        String token = jwtUtil.generateToken(
                new UserDetails(
                        customer.getEmail(),
                        "password",
                        UserType.CUSTOMER
                )
        );

        assertThrows(
                TokenException.class,
                () -> jwtUtil.checkUser(token, UserType.CUSTOMER)
        );
    }

    @Test
    void refreshedTokenUsesUpdatedEmail() throws Exception {
        Customer customer = new Customer();
        customer.setId(77);
        customer.setEmail("old@example.com");

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        when(customerRepository.findEmailForAuthById(77))
                .thenReturn(Optional.of("new@example.com"));

        when(customerRepository.existsByIdAndEmail(
                77, "new@example.com")).thenReturn(true);

        String originalToken = jwtUtil.generateToken(
                new UserDetails(
                        customer.getEmail(),
                        "password",
                        UserType.CUSTOMER
                )
        );

        String refreshedToken = jwtUtil.generateToken(
                originalToken
        );

        assertEquals(
                "new@example.com",
                jwtUtil.extractEmail(refreshedToken)
        );

        assertEquals(
                77,
                jwtUtil.checkUser(
                        refreshedToken,
                        UserType.CUSTOMER
                )
        );
    }

    @Test
    void administratorTokenIsAllowed() throws Exception {
        String token = jwtUtil.generateToken(
                new UserDetails(
                        "admin@admin.com",
                        "admin",
                        UserType.ADMIN
                )
        );

        assertEquals(
                0,
                jwtUtil.checkUser(token, UserType.ADMIN)
        );
    }

    @Test
    void invalidSignatureIsRejected() {
        String token = jwtUtil.generateToken(
                new UserDetails(
                        "admin@admin.com",
                        "admin",
                        UserType.ADMIN
                )
        );

        String otherSecret = Base64.getEncoder()
                .encodeToString(
                        "abcdef0123456789abcdef0123456789"
                                .getBytes(StandardCharsets.UTF_8)
                );

        JWTutil otherJwtUtil = new JWTutil(
                otherSecret,
                companyRepository,
                customerRepository
        );

        assertThrows(
                TokenException.class,
                () -> otherJwtUtil.checkUser(
                        token,
                        UserType.ADMIN
                )
        );
    }

    @Test
    void weakJwtKeyIsRejected() {
        String weakSecret = Base64.getEncoder()
                .encodeToString(
                        "short".getBytes(StandardCharsets.UTF_8)
                );

        assertThrows(
                IllegalStateException.class,
                () -> new JWTutil(
                        weakSecret,
                        companyRepository,
                        customerRepository
                )
        );
    }
}