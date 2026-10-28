
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.exception.TokenException;
import com.jb.spring_coupons_project.security.PasswordCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService extends ClientService {

    @Override
    public boolean login(String email, String password) {
        return "admin@admin.com".equals(email)
                && "admin".equals(password);
    }

    public void addCompany(Company company)
            throws CompanyException, TokenException {

        if (company == null ||
                company.getName() == null ||
                company.getName().isBlank() ||
                company.getEmail() == null ||
                company.getEmail().isBlank()) {
            throw new CompanyException(
                    "Company name and email are required"
            );
        }

        if (companyRepository.existsByName(company.getName())) {
            throw new CompanyException(
                    "The company already exists"
            );
        }

        if (companyRepository.existsByEmail(company.getEmail())) {
            throw new CompanyException(
                    "Email already exists"
            );
        }

        try {
            company.setPassword(
                    PasswordCodec.encode(company.getPassword())
            );
        } catch (IllegalArgumentException ex) {
            throw new CompanyException(ex.getMessage());
        }

        company.setId(0);
        company.setCoupons(null);

        companyRepository.save(company);
    }

    public void addCustomer(Customer customer)
            throws CustomerException, TokenException {

        if (customer == null ||
                customer.getEmail() == null ||
                customer.getEmail().isBlank()) {
            throw new CustomerException("Email is required");
        }

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerException(
                    "There is already a user with this email"
            );
        }

        try {
            customer.setPassword(
                    PasswordCodec.encode(customer.getPassword())
            );
        } catch (IllegalArgumentException ex) {
            throw new CustomerException(ex.getMessage());
        }

        customer.setId(0);
        customer.setCoupons(null);

        customerRepository.save(customer);
    }

    public Optional<Company> getOneCompany(int id)
            throws ExistsException, TokenException {

        return Optional.of(
                companyRepository.findById(id)
                        .orElseThrow(() ->
                                new ExistsException(
                                        "Company not exists"
                                )
                        )
        );
    }

    public List<Company> getAllCompanies()
            throws ExistsException, TokenException {

        List<Company> companies = companyRepository.findAll();

        if (companies.isEmpty()) {
            throw new ExistsException(
                    "There is no company exist"
            );
        }

        return companies;
    }

    public Optional<Customer> getOneCustomer(int id)
            throws ExistsException, TokenException {

        return Optional.of(
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new ExistsException(
                                        "Customer not exists"
                                )
                        )
        );
    }

    public List<Customer> getAllCustomers()
            throws ExistsException, TokenException {

        List<Customer> customers = customerRepository.findAll();

        if (customers.isEmpty()) {
            throw new ExistsException(
                    "There is no customer exists"
            );
        }

        return customers;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCompany(Company incoming)
            throws ExistsException, TokenException, CompanyException {

        Company existing = companyRepository.findById(incoming.getId())
                .orElseThrow(() ->
                        new ExistsException("Company not exists")
                );

        if (incoming.getName() != null &&
                !incoming.getName().equals(existing.getName())) {

            if (incoming.getName().isBlank()) {
                throw new CompanyException(
                        "Company name is required"
                );
            }

            if (companyRepository.existsByName(incoming.getName())) {
                throw new CompanyException(
                        "Company name already exists"
                );
            }

            existing.setName(incoming.getName());
        }

        if (incoming.getEmail() != null &&
                !incoming.getEmail().equals(existing.getEmail())) {

            if (incoming.getEmail().isBlank()) {
                throw new CompanyException("Email is required");
            }

            if (companyRepository.existsByEmail(incoming.getEmail())) {
                throw new CompanyException("Email already exists");
            }

            existing.setEmail(incoming.getEmail());
        }

        if (incoming.getPassword() != null &&
                !incoming.getPassword().isBlank()) {

            try {
                existing.setPassword(
                        PasswordCodec.encode(incoming.getPassword())
                );
            } catch (IllegalArgumentException ex) {
                throw new CompanyException(ex.getMessage());
            }
        }

        companyRepository.saveAndFlush(existing);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(Customer incoming)
            throws ExistsException, TokenException, CompanyException {

        Customer existing = customerRepository.findById(incoming.getId())
                .orElseThrow(() ->
                        new ExistsException("Customer not exists")
                );

        if (incoming.getEmail() != null &&
                !incoming.getEmail().equals(existing.getEmail())) {

            if (incoming.getEmail().isBlank()) {
                throw new CompanyException("Email is required");
            }

            if (customerRepository.existsByEmail(incoming.getEmail())) {
                throw new CompanyException("Email already exists");
            }

            existing.setEmail(incoming.getEmail());
        }

        if (incoming.getFirst_name() != null) {
            existing.setFirst_name(incoming.getFirst_name());
        }

        if (incoming.getLast_name() != null) {
            existing.setLast_name(incoming.getLast_name());
        }

        if (incoming.getPassword() != null &&
                !incoming.getPassword().isBlank()) {

            try {
                existing.setPassword(
                        PasswordCodec.encode(incoming.getPassword())
                );
            } catch (IllegalArgumentException ex) {
                throw new CompanyException(ex.getMessage());
            }
        }

        customerRepository.saveAndFlush(existing);
    }

    public void deleteCompany(int id)
            throws ExistsException, TokenException {

        if (!companyRepository.existsById(id)) {
            throw new ExistsException(
                    "Company not exists"
            );
        }

        companyRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(int id)
            throws ExistsException, TokenException {

        if (!customerRepository.existsById(id)) {
            throw new ExistsException(
                    "Customer not exists"
            );
        }

        customerRepository.deletePurchaseLinksByCustomerId(id);

        int deletedCustomers =
                customerRepository.deleteCustomerRowById(id);

        if (deletedCustomers != 1) {
            throw new IllegalStateException(
                    "Customer deletion failed for ID " + id
            );
        }
    }
}
