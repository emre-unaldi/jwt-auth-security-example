package com.unaldi.authservice.model;

import com.unaldi.authservice.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a refresh token in the system.
 * Refresh tokens are used to obtain new access tokens without re-authentication.
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Entity
@Table(name = "refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "token")
        },
        indexes = {
                @Index(name = "idx_refresh_token_token", columnList = "token"),
                @Index(name = "idx_refresh_token_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_token_expiry", columnList = "expiry_date"),
                @Index(name = "idx_refresh_token_revoked", columnList = "is_revoked")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    /**
     * The actual token string
     */
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    /**
     * User ID associated with this token
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * User email for audit purposes
     */
    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    /**
     * When this token expires
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * IP address from which the token was created
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent string from the client
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Device fingerprint for additional security
     */
    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    /**
     * Flag indicating if the token has been revoked
     */
    @Column(name = "is_revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    /**
     * Timestamp when the token was revoked
     */
    @Column(name = "revoked_date")
    private LocalDateTime revokedDate;

    /**
     * Reason for revocation
     */
    @Column(name = "revocation_reason", length = 255)
    private String revocationReason;

    /**
     * Number of times this token has been used
     */
    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    /**
     * Last time this token was used
     */
    @Column(name = "last_used_date")
    private LocalDateTime lastUsedDate;

    /**
     * Checks if the token is expired
     *
     * @return true if the token is expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * Checks if the token is valid (not expired and not revoked)
     *
     * @return true if the token is valid, false otherwise
     */
    public boolean isValid() {
        return !isExpired() && !revoked && !isDeleted();
    }

    /**
     * Revokes the token with a reason
     *
     * @param reason The reason for revocation
     */
    public void revoke(String reason) {
        this.revoked = true;
        this.revokedDate = LocalDateTime.now();
        this.revocationReason = reason;
    }

    /**
     * Increments the usage count and updates last used date
     */
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + getId() +
                ", userId=" + userId +
                ", expiryDate=" + expiryDate +
                ", revoked=" + revoked +
                '}';
    }
}
