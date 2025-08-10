package com.unaldi.authservice.mapper;

import com.unaldi.authservice.dto.response.RoleResponseDto;
import com.unaldi.authservice.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Role entity and DTOs
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Mapper(componentModel = "spring", uses = {PrivilegeMapper.class})
public interface RoleMapper {

    /**
     * Maps Role entity to RoleResponse DTO
     *
     * @param role Role entity
     * @return RoleResponse DTO
     */
    @Mapping(target = "privileges", source = "privileges")
    RoleResponseDto toRoleResponse(Role role);

    /**
     * Maps list of Role entities to list of RoleResponse DTOs
     *
     * @param roles List of Role entities
     * @return List of RoleResponse DTOs
     */
    List<RoleResponseDto> toRoleResponseList(List<Role> roles);

    /**
     * Maps set of Role entities to set of RoleResponse DTOs
     *
     * @param roles Set of Role entities
     * @return Set of RoleResponse DTOs
     */
    Set<RoleResponseDto> toRoleResponseSet(Set<Role> roles);

}