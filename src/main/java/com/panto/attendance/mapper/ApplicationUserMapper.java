package com.panto.attendance.mapper;

import com.panto.attendance.dto.ApplicationUserResponse;
import com.panto.attendance.model.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationUserMapper {
    @Mapping(target="email",expression = "java(getEmail(user))")
    @Mapping(target="fullName",expression = "java(getFullNameIfAvailable(user))")
    @Mapping(target="isAccountActiveAsString",expression = "java(convertStatusToString(user))")
    @Mapping(target="personnelId",expression = "java(getPersonnelIdIfAvailable(user))")
    ApplicationUserResponse mapToDtoResponse(ApplicationUser user);

    default Long getPersonnelIdIfAvailable(ApplicationUser user){
        return user.getPersonnel() == null ? null : user.getPersonnel().getId();
    }
    default String getFullNameIfAvailable(ApplicationUser user){
        if(user.getPersonnel() != null){
            return user.getPersonnel().getFirstName() + ' ' + user.getPersonnel().getLastName();
        }
        return "";
    }
    default String convertStatusToString(ApplicationUser user){
        return user.isAccountActive() ? "Active" : "Inactive";
    }
    default String getEmail(ApplicationUser user){
        if(user.getPersonnel() == null) return "";
        String email = user.getPersonnel().getEmail();
        return email == null ? "" : email;
    }
}
