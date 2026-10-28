
package com.jb.spring_coupons_project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordPrivacyTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void customerPasswordIsNeverSerialized() throws Exception {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setEmail("customer@example.com");
        customer.setPassword("private-password");

        JsonNode json = mapper.readTree(
                mapper.writeValueAsString(customer)
        );

        assertFalse(json.has("password"));
        assertEquals(
                "customer@example.com",
                json.get("email").asText()
        );

        assertFalse(customer.toString().contains("private-password"));
    }

    @Test
    void companyPasswordIsNeverSerialized() throws Exception {
        Company company = new Company();
        company.setId(1);
        company.setName("Demo Company");
        company.setEmail("company@example.com");
        company.setPassword("private-password");

        JsonNode json = mapper.readTree(
                mapper.writeValueAsString(company)
        );

        assertFalse(json.has("password"));
        assertEquals(
                "company@example.com",
                json.get("email").asText()
        );

        assertFalse(company.toString().contains("private-password"));
    }

    @Test
    void passwordsAreStillAcceptedFromRequests() throws Exception {
        Customer customer = mapper.readValue(
                "{\"email\":\"customer@example.com\"," +
                        "\"password\":\"new-password\"}",
                Customer.class
        );

        Company company = mapper.readValue(
                "{\"name\":\"Demo Company\"," +
                        "\"email\":\"company@example.com\"," +
                        "\"password\":\"new-password\"}",
                Company.class
        );

        assertEquals("new-password", customer.getPassword());
        assertEquals("new-password", company.getPassword());
    }

    @Test
    void customerCanUpdateDetailsWithoutChangingPassword()
            throws Exception {

        CustomerRepository repository = mock(CustomerRepository.class);
        CustomerService service = new CustomerService();

        ReflectionTestUtils.setField(
                service, "customerRepository", repository
        );

        Customer existing = new Customer();
        existing.setId(101);
        existing.setEmail("customer@example.com");
        existing.setPassword("existing-secret");

        when(repository.findById(101))
                .thenReturn(Optional.of(existing));

        Customer incoming = new Customer();
        incoming.setEmail("customer@example.com");
        incoming.setFirst_name("Updated");
        incoming.setLast_name("Customer");
        incoming.setPassword(null);

        service.updateCustomerDetails(101, incoming);

        assertEquals("existing-secret", existing.getPassword());
        assertEquals("Updated", existing.getFirst_name());

        verify(repository).saveAndFlush(existing);
    }

    @Test
    void companyCanUpdateDetailsWithoutChangingPassword()
            throws Exception {

        CompanyRepository repository = mock(CompanyRepository.class);
        CompanyService service = new CompanyService();

        ReflectionTestUtils.setField(
                service, "companyRepository", repository
        );

        Company existing = new Company();
        existing.setId(101);
        existing.setName("Demo");
        existing.setEmail("company@example.com");
        existing.setPassword("existing-secret");

        when(repository.findById(101))
                .thenReturn(Optional.of(existing));

        Company incoming = new Company();
        incoming.setEmail("company@example.com");
        incoming.setPassword("");

        service.updateCompanyDetails(101, incoming);

        assertEquals("existing-secret", existing.getPassword());
        verify(repository).saveAndFlush(existing);
    }

    @Test
    void adminUpdateDoesNotEraseCustomerPasswordOrPurchases()
            throws Exception {

        CustomerRepository repository = mock(CustomerRepository.class);
        AdminService service = new AdminService();

        ReflectionTestUtils.setField(
                service, "customerRepository", repository
        );

        Customer existing = new Customer();
        existing.setId(101);
        existing.setEmail("customer@example.com");
        existing.setFirst_name("Original");
        existing.setPassword("existing-secret");

        when(repository.findById(101))
                .thenReturn(Optional.of(existing));

        Customer incoming = new Customer();
        incoming.setId(101);
        incoming.setFirst_name("Updated");
        incoming.setPassword("");

        service.updateCustomer(incoming);

        assertEquals("existing-secret", existing.getPassword());
        assertEquals("Updated", existing.getFirst_name());

        verify(repository).saveAndFlush(existing);
    }

    @Test
    void adminUpdateDoesNotEraseCompanyPassword()
            throws Exception {

        CompanyRepository repository = mock(CompanyRepository.class);
        AdminService service = new AdminService();

        ReflectionTestUtils.setField(
                service, "companyRepository", repository
        );

        Company existing = new Company();
        existing.setId(101);
        existing.setName("Demo");
        existing.setEmail("company@example.com");
        existing.setPassword("existing-secret");

        when(repository.findById(101))
                .thenReturn(Optional.of(existing));

        Company incoming = new Company();
        incoming.setId(101);
        incoming.setPassword(null);

        service.updateCompany(incoming);

        assertEquals("existing-secret", existing.getPassword());
        verify(repository).saveAndFlush(existing);
    }
}
