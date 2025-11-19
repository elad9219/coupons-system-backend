package com.jb.spring_coupons_project.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Setter
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_seq")
    @SequenceGenerator(name = "coupon_seq", sequenceName = "coupon_seq", allocationSize = 1)
    @Column(name = "coupon_id")
    private int id;
    private int companyId;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "customers_vs_coupons", joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id"))
    @ToString.Exclude
    @JsonIgnore
    private Set<Customer> customers;

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private Category category;
    @Column(length = 25, nullable = false)
    private String title;
    @Column(length = 40, nullable = false)
    private String description;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate start_date;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate end_date;
    @Column(nullable = false)
    private int amount;
    @Column(nullable = false)
    private double price;
    private String image;

    // Added field to mark expired coupons without deletion
    @Column(nullable = false)
    private boolean expired = false;
}