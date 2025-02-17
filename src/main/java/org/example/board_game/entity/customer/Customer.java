package org.example.board_game.entity.customer;


import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.entity.cart.Cart;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.CustomerStatus;
import org.example.board_game.infrastructure.enums.Gender;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Getter
@Setter
@Table(name = "customer")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)

//public class Customer extends PrimaryEntity implements UserDetails {
public class Customer extends PrimaryEntity {

    @Column(name = "full_name", length = EntityProperties.LENGTH_NAME)
    String fullName;

    @Column(name = "email", length = EntityProperties.LENGTH_EMAIL, nullable = false)
    String email;

    @Column(name = "date_of_birth")
    Long dateOfBirth;

    @Column(name = "password", length = EntityProperties.LENGTH_PASSWORD, nullable = false)
    String password;

    @Column(name = "status")
    CustomerStatus status;

    @Column(name = "gender")
    Gender gender;

    @Column(name = "url_image")
    String image;

    @OneToMany(mappedBy = "customer")
    List<Address> addressList;

//    @OneToMany(mappedBy = "customer")
//    List<Order> orders;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    Cart cart;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(EntityProperties.ROLE_CUSTOMER));
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    @PrePersist
//    private void createCart() {
//        Cart cart = new Cart();
//        cart.setCustomer(this);
//        this.cart = cart;
//    }
}

