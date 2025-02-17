package org.example.board_game.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;

@Getter
@Setter
@Table(name = "publisher")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Publisher extends PrimaryEntity {

    @Column(name = "name", length = EntityProperties.LENGTH_NAME, nullable = false)
    String name;

    @Column(name = "description", length = EntityProperties.LENGTH_DESCRIPTION)
    String description;
}

