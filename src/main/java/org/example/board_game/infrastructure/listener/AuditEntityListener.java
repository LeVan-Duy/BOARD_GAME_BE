package org.example.board_game.infrastructure.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.AuditEntity;

import java.util.Calendar;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditEntityListener {

    @PrePersist
    void onCreate(AuditEntity entity) {
        entity.setCreatedAt(getCurrentTime());
        entity.setUpdatedAt(getCurrentTime());
    }

    @PreUpdate
    void onUpdate(AuditEntity entity) {
        entity.setUpdatedAt(getCurrentTime());
    }

    long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

}