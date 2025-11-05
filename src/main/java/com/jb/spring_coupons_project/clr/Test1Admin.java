package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.service.AdminService;
import com.jb.spring_coupons_project.util.TablePrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Order(1)
@RequiredArgsConstructor
public class Test1Admin implements CommandLineRunner {

    /**
     * Exists & not exists tests (to see exceptions)
     */

    private final AdminService adminService;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        if (adminService.login("admin@admin.com", "admin")) {
            System.out.println("\n\nAdmin connected\n\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        //CREATE
        Company mega = Company.builder()
                .name("mega")
                .email("mega@mega.com")
                .password("mega8203")
                .build();

        Company sano = Company.builder()
                .name("sano")
                .email("sano@sano.com")
                .password("sano9494")
                .build();

        Company sony = Company.builder()
                .name("sony")
                .email("sony@sony.com")
                .password("123")
                .build();

        Company issta = Company.builder()
                .name("issta")
                .email("issta@travel.com")
                .password("isstafly80")
                .build();

        Company isrotel = Company.builder()
                .name("isrotel")
                .email("isrotel@isrotel.com")
                .password("isrotelhotels")
                .build();


        List<Company> companyList = List.of(mega, sano, sony, issta, isrotel);
        System.out.println("saving companies");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");


        for (Company company : companyList) {
            if (!companyRepository.existsByEmail(company.getEmail())) {
                companyRepository.save(company);
            } else {
                System.out.println("Company " + company.getName() + " already exists, skipping...");
            }
        }



        Customer john = Customer.builder()
                .first_name("John")
                .last_name("Jackson")
                .email("john289@gmail.com")
                .password("john777")
                .build();

        Customer dan = Customer.builder()
                .first_name("Dan")
                .last_name("Kan")
                .email("dankan72@gmail.com")
                .password("123")
                .build();

        Customer mor = Customer.builder()
                .first_name("Mor")
                .last_name("Bon")
                .email("mor228@hotmail.com")
                .password("mormor")
                .build();

        Customer jacob = Customer.builder()
                .first_name("Jacob")
                .last_name("Kazenelenburgen")
                .email("jacobavinu1892@gmail.com")
                .password("jack29002!%#")
                .build();

        Customer sami = Customer.builder()
                .first_name("Sami")
                .last_name("Fireman")
                .email("sami372@gmail.com")
                .password("samiandsusu24392")
                .build();

        List<Customer> customerList = List.of(john, dan, mor, jacob, sami);
        System.out.println("saving customers");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");


        for (Customer customer : customerList) {
            if (!customerRepository.existsByEmail(customer.getEmail())) {
                customerRepository.save(customer);
            } else {
                System.out.println("Customer " + customer.getFirst_name() + " already exists, skipping...");
            }
        }



        //READ
        System.out.println();
        List<Customer> customers = customerRepository.findAll();
        System.out.println("\nFind all customers: \n");
        TablePrinter.print(customers);
        System.out.println("-----------------------------------------------------------------------------------------------------------------");


        List<Company> companies = companyRepository.findAll();
        System.out.println("\nFind all companies: \n");
        TablePrinter.print(companies);
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        Optional<Customer> customer3 = (customerRepository.findById(3));
        if (customer3.isPresent()) {
            System.out.println("\nFind customer by id: \n");
            TablePrinter.print(customer3.get());
        } else {
            System.out.println("\nCustomer not found\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        Optional<Customer> customer12 = (customerRepository.findById(12));
        if (customer12.isPresent()) {
            System.out.println("\nFind customer by id: \n");
            TablePrinter.print(customer12.get());
        } else {
            System.out.println("\nCustomer not found\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        Optional<Company> company2 = companyRepository.findById(2);
        if (company2.isPresent()) {
            System.out.println("\nFind company by id: \n");
            TablePrinter.print(company2.get());
        } else {
            System.out.println("\nCompany not found\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        Optional<Company> company7 = companyRepository.findById(7);
        if (company7.isPresent()) {
            System.out.println("\nFind company by id: \n");
            TablePrinter.print(company7.get());
        } else {
            System.out.println("\nCompany not found\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        //UPDATE
        Optional<Customer> customer4Opt = customerRepository.findById(4);
        if (customer4Opt.isPresent()) {
            Customer customer4 = customer4Opt.get();
            customer4.setEmail("shalom@bye.com");
            customerRepository.save(customer4);
            System.out.println("\nUpdate coupon: \n");
            System.out.println("Customer " + customer4.getFirst_name() + " " +customer4.getLast_name() + " updated");
        } else {
            System.out.println("\nNo customer with id 4\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        //DELETE
        Optional<Customer> customer5 = customerRepository.findById(5);
        System.out.println("\nDelete coupon: \n");
        if (customerRepository.existsById(5)) {
            customerRepository.deleteById(5);
            System.out.println("\nCustomer deleted\n");
        } else {
            System.out.println("\nCustomer not found\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        System.out.println("\nGet all companies and customers again: \n");
        TablePrinter.print(adminService.getAllCompanies());
        System.out.println();
        TablePrinter.print(adminService.getAllCustomers());
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
    }
}