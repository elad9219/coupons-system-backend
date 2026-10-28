
package com.jb.spring_coupons_project.security;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.service.AdminService;
import com.jb.spring_coupons_project.service.CompanyService;
import com.jb.spring_coupons_project.service.CustomerService;
import com.jb.spring_coupons_project.service.GuestService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PasswordHashingTest {

    @Test
    void bcryptHashesPasswordsWithIndependentSalts() {
        String first = PasswordCodec.encode("1234");
        String second = PasswordCodec.encode("1234");

        assertNotEquals("1234", first);
        assertNotEquals(first, second);
        assertEquals(60, first.length());

        assertTrue(PasswordCodec.isEncoded(first));
        assertTrue(PasswordCodec.matches("1234", first));
        assertFalse(PasswordCodec.matches("wrong", first));
        assertFalse(PasswordCodec.matches("1234", "1234"));
    }

    @Test
    void guestRegistrationHashesPasswordAndIgnoresClientIdentity()
            throws Exception {

        CustomerRepository repository =
                mock(CustomerRepository.class);

        GuestService service = new GuestService();

        ReflectionTestUtils.setField(
                service, "customerRepository", repository
        );

        Customer customer = new Customer();
        customer.setId(999);
        customer.setEmail("new@example.com");
        customer.setPassword("1234");

        service.register(customer);

        assertEquals(0, customer.getId());
        assertNull(customer.getCoupons());
        assertTrue(PasswordCodec.matches(
                "1234", customer.getPassword()
        ));

        verify(repository).save(customer);
    }

    @Test
    void administratorCreatesAccountsWithHashedPasswords()
            throws Exception {

        CompanyRepository companyRepository =
                mock(CompanyRepository.class);

        CustomerRepository customerRepository =
                mock(CustomerRepository.class);

        AdminService service = new AdminService();

        ReflectionTestUtils.setField(
                service, "companyRepository", companyRepository
        );

        ReflectionTestUtils.setField(
                service, "customerRepository", customerRepository
        );

        Company company = new Company();
        company.setName("New Company");
        company.setEmail("company@example.com");
        company.setPassword("company-secret");

        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setPassword("customer-secret");

        service.addCompany(company);
        service.addCustomer(customer);

        assertTrue(PasswordCodec.matches(
                "company-secret", company.getPassword()
        ));

        assertTrue(PasswordCodec.matches(
                "customer-secret", customer.getPassword()
        ));

        verify(companyRepository).save(company);
        verify(customerRepository).save(customer);
    }

    @Test
    void loginChecksStoredHashesAndRejectsWrongPasswords() {
        CustomerRepository customerRepository =
                mock(CustomerRepository.class);

        CompanyRepository companyRepository =
                mock(CompanyRepository.class);

        CustomerService customerService = new CustomerService();
        CompanyService companyService = new CompanyService();

        ReflectionTestUtils.setField(
                customerService, "customerRepository", customerRepository
        );

        ReflectionTestUtils.setField(
                companyService, "companyRepository", companyRepository
        );

        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setPassword(PasswordCodec.encode("1234"));

        Company company = new Company();
        company.setEmail("company@example.com");
        company.setPassword(PasswordCodec.encode("1234"));

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        when(companyRepository.findByEmail(company.getEmail()))
                .thenReturn(company);

        assertTrue(customerService.login(
                "customer@example.com", "1234"
        ));

        assertFalse(customerService.login(
                "customer@example.com", "wrong"
        ));

        assertTrue(companyService.login(
                "company@example.com", "1234"
        ));

        assertFalse(companyService.login(
                "company@example.com", "wrong"
        ));

        verify(customerRepository, never())
                .findByEmailAndPassword(anyString(), anyString());

        verify(companyRepository, never())
                .findByEmailAndPassword(anyString(), anyString());
    }

    @Test
    void updatingPasswordsStoresNewHashes()
            throws Exception {

        CustomerRepository customerRepository =
                mock(CustomerRepository.class);

        CompanyRepository companyRepository =
                mock(CompanyRepository.class);

        CustomerService customerService = new CustomerService();
        CompanyService companyService = new CompanyService();

        ReflectionTestUtils.setField(
                customerService, "customerRepository", customerRepository
        );

        ReflectionTestUtils.setField(
                companyService, "companyRepository", companyRepository
        );

        Customer customer = new Customer();
        customer.setId(101);
        customer.setEmail("customer@example.com");
        customer.setPassword(PasswordCodec.encode("old-secret"));

        when(customerRepository.findById(101))
                .thenReturn(Optional.of(customer));

        Customer customerUpdate = new Customer();
        customerUpdate.setEmail("customer@example.com");
        customerUpdate.setFirst_name("Updated");
        customerUpdate.setLast_name("User");
        customerUpdate.setPassword("new-secret");

        customerService.updateCustomerDetails(101, customerUpdate);

        assertTrue(PasswordCodec.matches(
                "new-secret", customer.getPassword()
        ));

        assertFalse(PasswordCodec.matches(
                "old-secret", customer.getPassword()
        ));

        Company company = new Company();
        company.setId(202);
        company.setName("Demo");
        company.setEmail("company@example.com");
        company.setPassword(PasswordCodec.encode("old-secret"));

        when(companyRepository.findById(202))
                .thenReturn(Optional.of(company));

        Company companyUpdate = new Company();
        companyUpdate.setEmail("company@example.com");
        companyUpdate.setPassword("new-secret");

        companyService.updateCompanyDetails(202, companyUpdate);

        assertTrue(PasswordCodec.matches(
                "new-secret", company.getPassword()
        ));

        verify(customerRepository).saveAndFlush(customer);
        verify(companyRepository).saveAndFlush(company);
    }

    @Test
    void startupMigrationHashesLegacyPasswordsOnlyOnce()
            throws Exception {

        DriverManagerDataSource dataSource =
                new DriverManagerDataSource(
                        "jdbc:h2:mem:password_migration_test;" +
                                "DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                        "sa",
                        ""
                );

        dataSource.setDriverClassName("org.h2.Driver");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.execute(
                "CREATE TABLE companies (" +
                        "id INTEGER PRIMARY KEY, " +
                        "password VARCHAR(100) NOT NULL)"
        );

        jdbcTemplate.execute(
                "CREATE TABLE customers (" +
                        "customer_id INTEGER PRIMARY KEY, " +
                        "password VARCHAR(100) NOT NULL)"
        );

        String existingHash =
                PasswordCodec.encode("already-hashed");

        jdbcTemplate.update(
                "INSERT INTO companies (id, password) VALUES (?, ?)",
                1,
                "legacy-password"
        );

        jdbcTemplate.update(
                "INSERT INTO customers (customer_id, password) " +
                        "VALUES (?, ?)",
                2,
                existingHash
        );

        PasswordHashMigration migration =
                new PasswordHashMigration(jdbcTemplate);

        migration.run();

        String migratedHash = jdbcTemplate.queryForObject(
                "SELECT password FROM companies WHERE id = ?",
                String.class,
                1
        );

        String unchangedHash = jdbcTemplate.queryForObject(
                "SELECT password FROM customers WHERE customer_id = ?",
                String.class,
                2
        );

        assertNotNull(migratedHash);
        assertTrue(PasswordCodec.isEncoded(migratedHash));
        assertTrue(PasswordCodec.matches(
                "legacy-password", migratedHash
        ));

        assertEquals(
                existingHash,
                unchangedHash,
                "Already hashed passwords must not be modified"
        );

        migration.run();

        String hashAfterSecondRun = jdbcTemplate.queryForObject(
                "SELECT password FROM companies WHERE id = ?",
                String.class,
                1
        );

        assertEquals(
                migratedHash,
                hashAfterSecondRun,
                "Running the migration twice must not rehash passwords"
        );
    }
}
