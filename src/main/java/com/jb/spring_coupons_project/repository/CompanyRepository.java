package com.jb.spring_coupons_project.repository;

import com.jb.spring_coupons_project.beans.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Company findByEmail(String email);

    Optional<Company> findByEmailAndPassword(
            String email,
            String password
    );

    boolean existsByName(String name);

    boolean existsCompanyByEmailAndPassword(
            String email,
            String password
    );

    boolean existsByEmail(String email);

    boolean existsByIdAndEmail(int id, String email);

    @Query("SELECT c.email FROM Company c WHERE c.id = ?1")
    Optional<String> findEmailForAuthById(int id);
}