package org.example.board_game.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;

@Getter
@Setter
@Table(name = "product_category")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCategory extends PrimaryEntity {

    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Category category;

    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Product product;

    @Column(name = "description", length = EntityProperties.LENGTH_DESCRIPTION)
    String description;
}

