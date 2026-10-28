package com.jb.spring_coupons_project.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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

    private int companyId;

    // The inverse side of Customer.coupons.
    // Shared customer records must never be cascade-deleted.
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