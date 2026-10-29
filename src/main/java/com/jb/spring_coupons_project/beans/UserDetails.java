
package com.jb.spring_coupons_project.beans;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDetails {

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}
