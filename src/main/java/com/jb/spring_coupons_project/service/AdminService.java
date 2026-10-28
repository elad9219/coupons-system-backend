package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.exception.TokenException;
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
        return email.equals("admin@admin.com")
                && password.equals("admin");
    }

    public void addCompany(Company company)
            throws CompanyException, TokenException {

        if (companyRepository.existsByName(company.getName())) {
            throw new CompanyException(
                    "The company is already exists"
            );
        }

        companyRepository.save(company);
        System.out.println("company added");
    }

    public void addCustomer(Customer customer)
            throws CustomerException, TokenException {

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerException(
                    "There is already a user with this email"
            );
        }

        customerRepository.save(customer);
        System.out.println("customer added");
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

    public void updateCompany(Company company)
            throws ExistsException, TokenException, CompanyException {

        if (!companyRepository.existsById(company.getId())) {
            throw new ExistsException(
                    "Company not exists"
            );
        }

        companyRepository.save(company);
        System.out.println("Company updated");
    }

    public void updateCustomer(Customer customer)
            throws ExistsException, TokenException, CompanyException {

        if (!customerRepository.existsById(customer.getId())) {
            throw new ExistsException(
                    "Customer not exists"
            );
        }

        customerRepository.save(customer);
        System.out.println("Customer updated");
    }

    public void deleteCompany(int id)
            throws ExistsException, TokenException {

        if (!companyRepository.existsById(id)) {
            throw new ExistsException(
                    "Company not exists"
            );
        }

        companyRepository.deleteById(id);
        System.out.println("Company deleted (and all its coupons)");
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(int id)
            throws ExistsException, TokenException {

        if (!customerRepository.existsById(id)) {
            throw new ExistsException(
                    "Customer not exists"
            );
        }

        // Remove join-table relationships before deleting the customer.
        // Both operations participate in the same transaction.
        customerRepository.deletePurchaseLinksByCustomerId(id);

        int deletedCustomers =
                customerRepository.deleteCustomerRowById(id);

        // Never report a successful deletion if no customer row
        // was actually removed.
        if (deletedCustomers != 1) {
            throw new IllegalStateException(
                    "Customer deletion failed for ID " + id
            );
        }
    }
}