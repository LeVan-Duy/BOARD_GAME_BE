package org.example.board_game.entity.base;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.auth.utils.AuthHelper;
import org.example.board_game.infrastructure.listener.AuditEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AuditEntity {

    @Column(name = "created_at", updatable = false)
    Long createdAt;

    @Column(name = "updated_at")
    Long updatedAt;

    @Column(name = "created_by", updatable = false)
    String createdBy;

    @Column(name = "updated_by")
    String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdBy = AuthHelper.getUsername();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedBy = AuthHelper.getUsername();
    }
}