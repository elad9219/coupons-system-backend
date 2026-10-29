
package com.jb.spring_coupons_project.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "coupon_seq"
    )
    @SequenceGenerator(
            name = "coupon_seq",
            sequenceName = "coupon_seq",
            allocationSize = 1
    )
    @Column(name = "coupon_id")
    private int id;

    @Column(name = "company_id")
    private int companyId;

    @ManyToMany(mappedBy = "coupons", fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnore
    private Set<Customer> customers;

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private Category category;

    @Column(length = 25, nullable = false)
    private String title;

    @Column(length = 1024, nullable = false)
    private String description;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate start_date;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate end_date;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private double price;

    @Column(length = 1024)
    private String image;

    @Column(nullable = false)
    private boolean expired = false;
}
