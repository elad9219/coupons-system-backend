package com.jb.spring_coupons_project.controller;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.beans.UserType;
import com.jb.spring_coupons_project.exception.CompanyDeletionException;
import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.exception.TokenException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.security.JWTutil;
import com.jb.spring_coupons_project.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final JWTutil jwtUtil;

    @PostMapping("/addCompany")
    public ResponseEntity<?> addCompany(
            @RequestHeader("Authorization") String token,
            @RequestBody Company company)
            throws LoginException, TokenException, CompanyException {

        jwtUtil.checkUser(token, UserType.ADMIN);
        adminService.addCompany(company);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(company.getName() + " added.");
    }

    @PostMapping("/addCustomer")
    public ResponseEntity<?> addCustomer(
            @RequestHeader("Authorization") String token,
            @RequestBody Customer customer)
            throws LoginException, TokenException, CustomerException {

        jwtUtil.checkUser(token, UserType.ADMIN);
        adminService.addCustomer(customer);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        customer.getFirst_name() + " " +
                                customer.getLast_name() + " added."
                );
    }

    @GetMapping("/getAllCompanies")
    public ResponseEntity<?> getAllCompanies(
            @RequestHeader("Authorization") String token)
            throws LoginException, TokenException,
            CompanyException, ExistsException {

        jwtUtil.checkUser(token, UserType.ADMIN);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getAllCompanies());
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<?> getAllCustomers(
            @RequestHeader("Authorization") String token)
            throws LoginException, TokenException,
            CustomerException, ExistsException {

        jwtUtil.checkUser(token, UserType.ADMIN);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getAllCustomers());
    }

    @GetMapping("/getOneCompany/{id}")
    public ResponseEntity<?> getOneCompany(
            @RequestHeader("Authorization") String token,
            @PathVariable int id)
            throws LoginException, TokenException,
            CompanyException, ExistsException {

        jwtUtil.checkUser(token, UserType.ADMIN);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getOneCompany(id));
    }

    @GetMapping("/getOneCustomer/{id}")
    public ResponseEntity<?> getOneCustomer(
            @RequestHeader("Authorization") String token,
            @PathVariable int id)
            throws LoginException, TokenException, ExistsException {

        jwtUtil.checkUser(token, UserType.ADMIN);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getOneCustomer(id));
    }

    @PutMapping("/updateCompany")
    public ResponseEntity<?> updateCompany(
            @RequestHeader("Authorization") String token,
            @RequestBody Company company)
            throws LoginException, TokenException,
            CompanyException, ExistsException {

        jwtUtil.checkUser(token, UserType.ADMIN);
        adminService.updateCompany(company);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(company.getName() + " updated.");
    }

    @PutMapping("/updateCustomer")
    public ResponseEntity<?> updateCustomer(
            @RequestHeader("Authorization") String token,
            @RequestBody Customer customer)
            throws LoginException, TokenException,
            CustomerException, ExistsException, CompanyException {

        jwtUtil.checkUser(token, UserType.ADMIN);
        adminService.updateCustomer(customer);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        customer.getFirst_name() + " " +
                                customer.getLast_name() + " updated."
                );
    }

    @DeleteMapping("/deleteCompany/{id}")
    public ResponseEntity<?> deleteCompany(
            @RequestHeader("Authorization") String token,
            @PathVariable int id)
            throws LoginException, TokenException,
            CompanyException, ExistsException, CompanyDeletionException {

        jwtUtil.checkUser(token, UserType.ADMIN);

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new ExistsException("Company not exists")
                );

        adminService.deleteCompany(id);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Company " + company.getName() + " deleted.");
    }

    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<?> deleteCustomer(
            @RequestHeader("Authorization") String token,
            @PathVariable int id)
            throws LoginException, TokenException,
            CustomerException, ExistsException {

        jwtUtil.checkUser(token, UserType.ADMIN);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ExistsException("Customer not exists")
                );

        adminService.deleteCustomer(id);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        "Customer " + customer.getFirst_name() +
                                " " + customer.getLast_name() + " deleted."
                );
    }
}