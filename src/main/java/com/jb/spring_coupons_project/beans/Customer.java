package com.jb.spring_coupons_project.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "customers")
@ToString
public class Customer {
    @Id //Primary key
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq", allocationSize = 1)
    @Column(name = "customer_id")
    private int id;
    @Column(length = 40, nullable = false)
    private String first_name;
    @Column(length = 40, nullable = false)
    private String last_name;
    @Column(length = 40, nullable = false)
    private String email;
    @Column(length = 40, nullable = false)
    private String password;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "customers_vs_coupons", joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id"))
    @ToString.Exclude
    @JsonIgnore
    private Set<Coupon> coupons;
}