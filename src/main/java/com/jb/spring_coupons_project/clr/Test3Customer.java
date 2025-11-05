package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.service.CustomerService;
import com.jb.spring_coupons_project.util.TablePrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@RequiredArgsConstructor
public class Test3Customer implements CommandLineRunner {

    /**
     * Exists & not exists tests (to see exceptions)
     */

    public final CustomerService customerService;
    public final CouponRepository couponRepository;
    public final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {

        Customer customer = customerRepository.getById(2);
        customerService.login(customer.getEmail(), customer.getPassword());
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println();

        System.out.println("Purchase coupon: ");
        try {
            customerService.purchaseCoupon(2);
            System.out.println();
            customerService.purchaseCoupon(3);
            System.out.println();
            customerService.purchaseCoupon(14);
        } catch (ExistsException exception) {
            System.out.println("There are no coupons exists.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        System.out.println("\nGet all customer coupons: \n");
        try {
            TablePrinter.print(customerService.getCustomerCoupons());
        } catch (ExistsException exception) {
            System.out.println("There are no coupons for this customer.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("\nGet all customer coupons by category: \n");
        try {
            TablePrinter.print(customerService.getCustomerCouponsByCategory(Category.ELECTRICITY));
            TablePrinter.print(customerService.getCustomerCouponsByCategory(Category.BOWLING));
        } catch (ExistsException exception) {
            System.out.println("There are no coupons for this category.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("\nGet all customer coupons by maximum price: \n");
        try {
            TablePrinter.print(customerService.getCustomerCouponsByMaxPrice(200));
            TablePrinter.print(customerService.getCustomerCouponsByMaxPrice(6000));
            TablePrinter.print(customerService.getCustomerCouponsByMaxPrice(15));
        } catch (ExistsException exception) {
            System.out.println("There are no coupons for this maximum price.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("\nGet customer details: \n");
        System.out.println(customerService.getCustomerDetails());
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
    }
}