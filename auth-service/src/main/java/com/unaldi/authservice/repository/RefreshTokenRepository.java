package com.unaldi.authservice.repository;

import com.unaldi.authservice.model.RefreshToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RefreshToken entity operations.
 * Provides CRUD operations and custom queries for refresh token management.
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its token string
     *
     * @param token The token string
     * @return Optional containing the refresh token if found
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token AND rt.deleted = false")
    Optional<RefreshToken> findByTokenAndNotDeleted(@Param("token") String token);

    /**
     * Finds all valid tokens for a user
     *
     * @param userId The user ID
     * @return List of valid refresh tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.revoked = false AND rt.expiryDate > :now AND rt.deleted = false")
    List<RefreshToken> findValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Finds all tokens for a user with pagination
     *
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of refresh tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.deleted = false")
    Page<RefreshToken> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Revokes all tokens for a user
     *
     * @param userId The user ID
     * @param reason The revocation reason
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedDate = CURRENT_TIMESTAMP, rt.revocationReason = :reason WHERE rt.userId = :userId AND rt.revoked = false AND rt.deleted = false")
    void revokeAllTokensByUserId(@Param("userId") Long userId, @Param("reason") String reason);

    /**
     * Revokes a specific token
     *
     * @param token The token string
     * @param reason The revocation reason
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedDate = CURRENT_TIMESTAMP, rt.revocationReason = :reason WHERE rt.token = :token AND rt.revoked = false")
    void revokeToken(@Param("token") String token, @Param("reason") String reason);

    /**
     * Deletes expired tokens
     *
     * @param expiryDate The cutoff date
     * @return Number of deleted tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :expiryDate")
    int deleteExpiredTokens(@Param("expiryDate") LocalDateTime expiryDate);

    /**
     * Finds tokens by device fingerprint
     *
     * @param deviceFingerprint The device fingerprint
     * @return List of tokens for the device
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.deviceFingerprint = :deviceFingerprint AND rt.deleted = false")
    List<RefreshToken> findByDeviceFingerprint(@Param("deviceFingerprint") String deviceFingerprint);

    /**
     * Counts active tokens for a user
     *
     * @param userId The user ID
     * @param now Current timestamp
     * @return Count of active tokens
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.userId = :userId AND rt.revoked = false AND rt.expiryDate > :now AND rt.deleted = false")
    long countActiveTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Finds tokens created from a specific IP address
     *
     * @param ipAddress The IP address
     * @param startDate Start date for the search
     * @return List of tokens from the IP
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.ipAddress = :ipAddress AND rt.createdDate >= :startDate AND rt.deleted = false")
    List<RefreshToken> findByIpAddressAfterDate(@Param("ipAddress") String ipAddress, @Param("startDate") LocalDateTime startDate);

    /**
     * Updates the usage count and last used date
     *
     * @param tokenId The token ID
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.usageCount = rt.usageCount + 1, rt.lastUsedDate = CURRENT_TIMESTAMP WHERE rt.id = :tokenId")
    void incrementUsageCount(@Param("tokenId") Long tokenId);

    /**
     * Finds tokens that haven't been used for a specified period
     *
     * @param inactiveSince Date threshold
     * @return List of inactive tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE (rt.lastUsedDate < :inactiveSince OR rt.lastUsedDate IS NULL) AND rt.createdDate < :inactiveSince AND rt.revoked = false AND rt.deleted = false")
    List<RefreshToken> findInactiveTokens(@Param("inactiveSince") LocalDateTime inactiveSince);

    /**
     * Checks if a valid token exists for user and device
     *
     * @param userId The user ID
     * @param deviceFingerprint The device fingerprint
     * @param now Current timestamp
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(rt) > 0 FROM RefreshToken rt WHERE rt.userId = :userId AND rt.deviceFingerprint = :deviceFingerprint AND rt.revoked = false AND rt.expiryDate > :now AND rt.deleted = false")
    boolean existsValidTokenForUserAndDevice(@Param("userId") Long userId, @Param("deviceFingerprint") String deviceFingerprint, @Param("now") LocalDateTime now);

}
