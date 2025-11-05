package com.jb.spring_coupons_project.beans;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", sequenceName = "company_seq", allocationSize = 1)
    private int id;
    @Column(length = 40, nullable = false, unique = true)
    private String name;
    @Column(length = 40, nullable = false, unique = true)
    private String email;
    @Column(length = 40, nullable = false)
    private String password;

    @OneToMany(mappedBy = "companyId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Coupon> coupons;
}