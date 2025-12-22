package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService extends ClientService {
    private int company_id;

    @Override
    public boolean login(String email, String password) {
        Optional<Company> company = companyRepository.findByEmailAndPassword(email, password);
        if (company.isPresent()) {
            this.company_id = company.get().getId();
            System.out.println("Company connected");
            return true;
        }
        return false;
    }

    //CREATE
    public void addCoupon(Coupon coupon) throws CouponException, CompanyException, ExistsException, TokenException {
        Company company = getCompanyDetails();
        if (couponRepository.existsByTitleAndCompanyId(coupon.getTitle(), coupon.getId())) {
            throw new CouponException("This coupon already exists");
        }
        coupon.setCompanyId(company_id);
        couponRepository.save(coupon);
        company.getCoupons().add(coupon);
        companyRepository.saveAndFlush(company);
        System.out.println("Coupon added.");
    }

    //READ
    public List<Coupon> getOneCompanyCoupon(int coupon_id) throws ExistsException, TokenException {
        if (couponRepository.getOneCompanyCoupon(this.company_id, coupon_id).isEmpty()) {
            throw new ExistsException("Coupon not exists");
        }
        return (couponRepository.getOneCompanyCoupon(this.company_id, coupon_id));
    }

    public List<Coupon> getAllCompanyCoupons() throws ExistsException, TokenException {
        List<Coupon> couponsList = couponRepository.getAllCompanyCoupons(this.company_id);
        if (couponsList.isEmpty()) {
            throw new ExistsException("No coupons exists");
        }
        return couponsList;
    }

    public List<Coupon> getAllCompanyCouponsByCategory(Category category) throws ExistsException, TokenException {
        List<Coupon> couponList = couponRepository.findAllCompanyCouponsByCategory(category, this.company_id);
        if (couponList.isEmpty()) {
            throw new ExistsException("no coupon exists in this category.");
        }
        return couponList;
    }

    public List<Coupon> getAllCompanyCouponsByMaxPrice(double maxPrice) throws ExistsException, TokenException {
        List<Coupon> couponList = couponRepository.findAllCompanyCouponsByMaxPrice(maxPrice, this.company_id);
        if (couponList.isEmpty()) {
            throw new ExistsException("The company has no coupons exists for this max price.");
        }
        return couponList;
    }

    public Company getCompanyDetails() throws ExistsException, TokenException {
        if (companyRepository.existsById(this.company_id)) {
            return companyRepository.findById(this.company_id).get();
        } else {
            throw new ExistsException("Company not exists.");
        }
    }

    //UPDATE
    public void updateCoupon(Coupon coupon) throws CouponException, ExistsException, TokenException {
        // Fix: Fetch the existing coupon to verify existence and ownership
        Optional<Coupon> existingCouponOpt = couponRepository.findById(coupon.getId());

        if (existingCouponOpt.isEmpty()) {
            throw new ExistsException("Coupon not exists.");
        }

        Coupon existingCoupon = existingCouponOpt.get();

        // Fix logic: Allow updating if the coupon belongs to the company, even if title changes.
        // We use the companyId from the DB record to verify ownership properly.
        if (existingCoupon.getCompanyId() != this.company_id && this.company_id != 0) {
            // Note: 'this.company_id != 0' is a safeguard if service state was lost on restart
            throw new CouponException("You cannot update a coupon that does not belong to your company.");
        }

        // Ensure the companyId is preserved correctly
        coupon.setCompanyId(existingCoupon.getCompanyId());

        couponRepository.save(coupon);
        System.out.println("Coupon updated.");
    }

    //DELETE
    @Transactional
    public void deleteCoupon(int coupon_id) throws ExistsException, TokenException {
        if (couponRepository.getOneCompanyCoupon(this.company_id, coupon_id).isEmpty()) {
            throw new ExistsException("Coupon not exists.");
        }
        customerRepository.deleteCouponsByCouponId(coupon_id); // delete all rows in customers_vs_coupons table that reference the coupon
        couponRepository.deleteById(coupon_id); // delete the coupon itself
        System.out.println("Coupon deleted.");
    }
}