package org.example.board_game.entity.voucher;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.VoucherStatus;
import org.example.board_game.infrastructure.enums.VoucherType;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Table(name = "voucher")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher extends PrimaryEntity {

    @Column(name = "code", length = EntityProperties.LENGTH_CODE, nullable = false)
    String code;

    @Column(name = "name", length = EntityProperties.LENGTH_NAME, nullable = false)
    @Nationalized
    String name;

    @Column(name = "status")
    VoucherStatus status;

    @Column(name = "type")
    VoucherType type;

    @Column(name = "value")
    float value;

    @Column(name = "voucher_constraint")
    float constraint;

    @Column(name = "quantity")
    int quantity;

    @Column(name = "start_date", nullable = false)
    Long startDate;

    @Column(name = "end_date", nullable = false)
    Long endDate;

    @Column(name = "url_image")
    String image;

    @Column(name = "description", length = EntityProperties.LENGTH_DESCRIPTION)
    String description;
}

