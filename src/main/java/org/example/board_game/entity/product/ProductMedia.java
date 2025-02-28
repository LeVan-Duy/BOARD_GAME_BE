package org.example.board_game.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;

@Getter
@Setter
@Table(name = "product_media")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductMedia extends PrimaryEntity {

    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Product product;

    @Column(name = "url")
    String url;

    @Column(name = "main_image")
    boolean mainImg;
}

