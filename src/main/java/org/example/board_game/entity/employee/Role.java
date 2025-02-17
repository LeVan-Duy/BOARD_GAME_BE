package org.example.board_game.entity.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Table(name = "role")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends PrimaryEntity {

    @Column(name = "name", length = EntityProperties.LENGTH_NAME, nullable = false, unique = true)
    @Nationalized
    private String name;
}
