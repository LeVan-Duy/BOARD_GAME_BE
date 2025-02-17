package org.example.board_game.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.ProductStatus;

@Getter
@Setter
@Table(name = "product")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends PrimaryEntity {

    @JoinColumn(name = "publisher_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Publisher publisher;

    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Author author;

    @Column(name = "name", nullable = false, length = EntityProperties.LENGTH_NAME)
    String name;

    @Column(name = "price")
    Float price;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "global_rating")
    Double globalRating;

    @Column(name = "number_of_players")
    Integer numberOfPlayers;

    @Column(name = "min_age")
    Integer minAge;

    @Column(name = "weight")
    Double weight;

    @Column(name = "min_time")
    Integer minTime;

    @Column(name = "max_time")
    Integer maxTime;

    @Column(name = "language")
    String language;

    @Column(name = "origin")
    String origin;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "status")
    ProductStatus status;

}

