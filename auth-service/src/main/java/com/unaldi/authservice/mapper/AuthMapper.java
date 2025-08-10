package com.unaldi.authservice.mapper;

import com.unaldi.authservice.model.RefreshToken;
import com.unaldi.authservice.dto.response.TokenValidationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for authentication-related entities and DTOs
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Mapper(componentModel = "spring")
public interface AuthMapper {

    /**
     * Maps RefreshToken entity to TokenValidationResponse
     *
     * @param refreshToken RefreshToken entity
     * @return TokenValidationResponse DTO
     */
    @Mapping(target = "valid", expression = "java(refreshToken.isValid())")
    @Mapping(target = "tokenType", constant = "REFRESH")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "email", source = "userEmail")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "privileges", ignore = true)
    @Mapping(target = "expiresAt", source = "expiryDate")
    @Mapping(target = "message", constant = "Refresh token validation completed")
    TokenValidationResponseDto toTokenValidationResponse(RefreshToken refreshToken);

}