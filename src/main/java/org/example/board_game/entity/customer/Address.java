package org.example.board_game.entity.customer;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;

@Getter
@Setter
@Table(name = "address")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address extends PrimaryEntity {

    @Column(name = "phone_number", length = EntityProperties.LENGTH_PHONE)
    String phoneNumber;

    @Column(name = "is_default", columnDefinition = "BOOLEAN DEFAULT false")
    Boolean isDefault;

    @Column(name = "district_id")
    Integer districtId;

    @Column(name = "province_id")
    Integer provinceId;

    @Column(name = "ward_code")
    Integer wardCode;

    @Column(name = "detail_address")
    String detailAddress;

    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Customer customer;
}
