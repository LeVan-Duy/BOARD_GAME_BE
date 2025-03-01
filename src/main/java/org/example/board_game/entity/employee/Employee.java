package org.example.board_game.entity.employee;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.EmployeeStatus;
import org.example.board_game.infrastructure.enums.Gender;
import org.example.board_game.infrastructure.enums.Role;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Table(name = "employee")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Employee extends PrimaryEntity implements UserDetails {

    @Column(name = "full_name", length = EntityProperties.LENGTH_NAME, nullable = false)
    @Nationalized
    String fullName;

    @Column(name = "email", length = EntityProperties.LENGTH_EMAIL, nullable = false)
    String email;

    @Column(name = "password", length = EntityProperties.LENGTH_PASSWORD, nullable = false)
    String password;

    @Column(name = "status")
    EmployeeStatus status;

    @Column(name = "address", length = EntityProperties.LENGTH_DESCRIPTION)
    @Nationalized
    String address;

    @Column(name = "gender")
    Gender gender;

    @Column(name = "phone_number", length = EntityProperties.LENGTH_PHONE)
    String phoneNumber;

    @Column(name = "url_image")
    String image;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", updatable = false)
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
