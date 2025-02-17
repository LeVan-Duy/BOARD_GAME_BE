package org.example.board_game.infrastructure.listener;

import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePrimaryEntityListener {

    @PrePersist
    void onCreate(PrimaryEntity entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
    }
}