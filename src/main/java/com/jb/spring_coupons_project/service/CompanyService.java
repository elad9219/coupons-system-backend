package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.exception.ExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService extends ClientService {

    @Override
    public boolean login(String email, String password) {
        return companyRepository
                .findByEmailAndPassword(email, password)
                .isPresent();
    }

    @Transactional
    public void addCoupon(int companyId, Coupon coupon)
            throws CouponException, CompanyException, ExistsException {

        if (!companyRepository.existsById(companyId)) {
            throw new ExistsException("Company not found");
        }

        if (couponRepository.existsByTitleAndCompanyId(
                coupon.getTitle(), companyId)) {
            throw new CouponException("This coupon already exists");
        }

        coupon.setId(0);
        coupon.setCompanyId(companyId);

        couponRepository.save(coupon);
    }

    public List<Coupon> getOneCompanyCoupon(
            int companyId,
            int couponId) throws ExistsException {

        List<Coupon> coupons = couponRepository.getOneCompanyCoupon(
                companyId, couponId
        );

        if (coupons.isEmpty()) {
            throw new ExistsException("Coupon not exists");
        }

        return coupons;
    }

    public List<Coupon> getAllCompanyCoupons(int companyId)
            throws ExistsException {

        List<Coupon> coupons = couponRepository
                .getAllCompanyCoupons(companyId);

        if (coupons.isEmpty()) {
            throw new ExistsException("No coupons exists");
        }

        return coupons;
    }

    public List<Coupon> getAllCompanyCouponsByCategory(
            int companyId,
            Category category) throws ExistsException {

        List<Coupon> coupons = couponRepository
                .findAllCompanyCouponsByCategory(category, companyId);

        if (coupons.isEmpty()) {
            throw new ExistsException(
                    "No coupons exist in this category"
            );
        }

        return coupons;
    }

    public List<Coupon> getAllCompanyCouponsByMaxPrice(
            int companyId,
            double maxPrice) throws ExistsException {

        List<Coupon> coupons = couponRepository
                .findAllCompanyCouponsByMaxPrice(maxPrice, companyId);

        if (coupons.isEmpty()) {
            throw new ExistsException(
                    "No coupons exist for this maximum price"
            );
        }

        return coupons;
    }

    public Company getCompanyDetails(int companyId)
            throws ExistsException {

        return companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ExistsException("Company not found")
                );
    }

    @Transactional
    public void updateCoupon(int companyId, Coupon incoming)
            throws CouponException, ExistsException {

        Coupon existing = couponRepository.findById(incoming.getId())
                .orElseThrow(() ->
                        new ExistsException("Coupon not exists")
                );

        if (existing.getCompanyId() != companyId) {
            throw new CouponException(
                    "You cannot update another company's coupon"
            );
        }

        if (!existing.getTitle().equals(incoming.getTitle()) &&
                couponRepository.existsByTitleAndCompanyId(
                        incoming.getTitle(), companyId)) {
            throw new CouponException(
                    "A coupon with this title already exists"
            );
        }

        existing.setCategory(incoming.getCategory());
        existing.setTitle(incoming.getTitle());
        existing.setDescription(incoming.getDescription());
        existing.setStart_date(incoming.getStart_date());
        existing.setEnd_date(incoming.getEnd_date());
        existing.setAmount(incoming.getAmount());
        existing.setPrice(incoming.getPrice());
        existing.setImage(incoming.getImage());

        couponRepository.save(existing);
    }

    @Transactional
    public void updateCompanyDetails(int companyId, Company incoming)
            throws CompanyException, ExistsException {

        Company existing = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ExistsException("Company not found")
                );

        if (!existing.getEmail().equals(incoming.getEmail())) {
            if (companyRepository.existsByEmail(incoming.getEmail())) {
                throw new CompanyException("Email already exists");
            }

            existing.setEmail(incoming.getEmail());
        }

        existing.setPassword(incoming.getPassword());

        companyRepository.saveAndFlush(existing);
    }

    @Transactional
    public void deleteCoupon(int companyId, int couponId)
            throws ExistsException {

        if (couponRepository.getOneCompanyCoupon(
                companyId, couponId).isEmpty()) {
            throw new ExistsException("Coupon not exists");
        }

        customerRepository.deleteCouponsByCouponId(couponId);
        couponRepository.deleteById(couponId);
    }
}