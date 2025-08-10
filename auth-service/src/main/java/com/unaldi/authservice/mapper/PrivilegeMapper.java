package com.unaldi.authservice.mapper;

import com.unaldi.authservice.dto.response.PrivilegeResponseDto;
import com.unaldi.authservice.model.Privilege;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Privilege entity and DTOs
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Mapper(componentModel = "spring")
public interface PrivilegeMapper {

    /**
     * Maps Privilege entity to PrivilegeResponse DTO
     *
     * @param privilege Privilege entity
     * @return PrivilegeResponse DTO
     */
    PrivilegeResponseDto toPrivilegeResponse(Privilege privilege);

    /**
     * Maps list of Privilege entities to list of PrivilegeResponse DTOs
     *
     * @param privileges List of Privilege entities
     * @return List of PrivilegeResponse DTOs
     */
    List<PrivilegeResponseDto> toPrivilegeResponseList(List<Privilege> privileges);

    /**
     * Maps set of Privilege entities to set of PrivilegeResponse DTOs
     *
     * @param privileges Set of Privilege entities
     * @return Set of PrivilegeResponse DTOs
     */
    Set<PrivilegeResponseDto> toPrivilegeResponseSet(Set<Privilege> privileges);

}