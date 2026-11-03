
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public abstract class ClientService {

    private static final String DEMO_COMPANY_EMAIL =
            "sony@contact.com";

    private static final String DEMO_CUSTOMER_EMAIL =
            "kobi@gmail.com";

    @Autowired
    protected CompanyRepository companyRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected CouponRepository couponRepository;

    @Value("${COUPONS_PUBLIC_DEMO:false}")
    protected boolean publicDemo;

    protected boolean isProtectedDemoCompany(Company company) {
        return publicDemo
                && company != null
                && DEMO_COMPANY_EMAIL.equalsIgnoreCase(
                company.getEmail()
        );
    }

    protected boolean isProtectedDemoCustomer(Customer customer) {
        return publicDemo
                && customer != null
                && DEMO_CUSTOMER_EMAIL.equalsIgnoreCase(
                customer.getEmail()
        );
    }

    public abstract boolean login(String email, String password);
}
