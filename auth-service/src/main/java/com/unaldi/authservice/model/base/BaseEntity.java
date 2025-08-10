package com.unaldi.authservice.model.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity class containing common fields for all entities.
 * Provides audit fields and soft delete functionality.
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    /**
     * Unique identifier for the entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Timestamp when the entity was created
     */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Email of the user who created the entity
     */
    @CreatedBy
    @Column(name = "created_by", length = 255, updatable = false)
    private String createdBy;

    /**
     * Timestamp when the entity was last modified
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    /**
     * Email of the user who last modified the entity
     */
    @LastModifiedBy
    @Column(name = "last_modified_by", length = 255)
    private String lastModifiedBy;

    /**
     * Soft delete flag. If true, the entity is considered deleted
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Timestamp when the entity was soft deleted
     */
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    /**
     * Email of the user who deleted the entity
     */
    @Column(name = "deleted_by", length = 255)
    private String deletedBy;

    /**
     * Version field for optimistic locking
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Lifecycle callback executed before entity persistence
     */
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    /**
     * Lifecycle callback executed before entity update
     */
    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    /**
     * Marks the entity as deleted with audit information
     *
     * @param deletedBy Email of the user performing the deletion
     */
    public void markAsDeleted(String deletedBy) {
        this.deleted = true;
        this.deletedDate = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
