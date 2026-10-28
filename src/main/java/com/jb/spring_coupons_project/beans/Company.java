
package com.jb.spring_coupons_project.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "company_seq"
    )
    @SequenceGenerator(
            name = "company_seq",
            sequenceName = "company_seq",
            allocationSize = 1
    )
    private int id;

    @Column(length = 40, nullable = false, unique = true)
    private String name;

    @Column(length = 40, nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @Column(length = 100, nullable = false)
    private String password;

    @OneToMany(
            mappedBy = "companyId",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @ToString.Exclude
    private List<Coupon> coupons;
}
