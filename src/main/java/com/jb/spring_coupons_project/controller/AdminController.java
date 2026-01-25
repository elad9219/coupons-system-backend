package com.jb.spring_coupons_project.controller;


import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.beans.UserType;
import com.jb.spring_coupons_project.exception.*;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.security.JWTutil;
import com.jb.spring_coupons_project.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/admin")   //http://localhost:8080/admin
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final CompanyRepository companyRepository;

    private final CustomerRepository customerRepository;

    private final JWTutil jwtUtil;




    //CREATE
    @PostMapping("/addCompany")
    public ResponseEntity<?> addCompany(@RequestHeader(name = "Authorization") String token, @RequestBody Company company) throws LoginException, TokenException, CompanyException {
        if (jwtUtil.validateToken(token)) {
            adminService.addCompany(company);
            return ResponseEntity.ok()
                    .header("Authorization", jwtUtil.generateToken(token))
                    .body(company.getName() + " added.");
        }
        throw new LoginException();
    }



    @PostMapping("/addCustomer")
    public ResponseEntity<?> addCustomer(@RequestHeader(name = "Authorization") String token, @RequestBody Customer customer) throws LoginException, TokenException, CustomerException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        adminService.addCustomer(customer);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customer.getFirst_name() + " " + customer.getLast_name() + " added.");
    }


    //READ
    @GetMapping("/getAllCompanies")
    public ResponseEntity<?> getAllCompanies(@RequestHeader(name = "Authorization") String token) throws LoginException, TokenException, CompanyException, ExistsException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getAllCompanies());
    }




    @GetMapping("/getAllCustomers") //  http://localhost:8080/customer/allcustomers
    public ResponseEntity<?> getAllCustomers(@RequestHeader(name = "Authorization") String token) throws LoginException, TokenException, CustomerException, ExistsException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getAllCustomers());
    }



    @GetMapping("/getOneCompany/{id}")
    public ResponseEntity<?> getOneCompany(@RequestHeader(name = "Authorization") String token, @PathVariable int id) throws LoginException, TokenException, CompanyException, ExistsException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getOneCompany(id));
    }


    @GetMapping("/getOneCustomer/{id}")    //http://localhost:8080/customer/2
    public ResponseEntity<?> getOneCustomer(@RequestHeader(name = "Authorization") String token, @PathVariable int id) throws LoginException, TokenException, ExistsException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(adminService.getOneCustomer(id));
    }


    //UPDATE
    @PutMapping("/updateCompany")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateCompany(@RequestHeader(name = "Authorization") String token, @RequestBody Company company) throws LoginException, TokenException, CompanyException, ExistsException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        adminService.updateCompany(company);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(company.getName() + " updated.");
    }


    @PutMapping("/updateCustomer")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateCustomer(@RequestHeader(name = "Authorization") String token, @RequestBody Customer customer) throws LoginException, TokenException, CustomerException, ExistsException, CompanyException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        adminService.updateCustomer(customer);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customer.getFirst_name() + " " + customer.getLast_name() + " updated.");
    }



    //DELETE
    @DeleteMapping("/deleteCompany/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<?> deleteCompany(@RequestHeader(name = "Authorization") String token, @PathVariable int id) throws LoginException, TokenException, CompanyException, ExistsException, CompanyDeletionException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        Company company = companyRepository.findById(id).orElse(null);
        adminService.deleteCompany(id);
        assert company != null;
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Company "+company.getName()+" deleted.");
    }



    @DeleteMapping("/deleteCustomer/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<?> deleteCustomer(@RequestHeader(name = "Authorization") String token, @PathVariable int id) throws LoginException, TokenException, CustomerException, ExistsException {
        jwtUtil.checkUser(token, UserType.ADMIN);
        Customer customer = customerRepository.findById(id).orElse(null);
        adminService.deleteCustomer(id);
        assert customer != null;
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Customer "+customer.getFirst_name()+" "+customer.getLast_name()+ " deleted.");
    }
}
