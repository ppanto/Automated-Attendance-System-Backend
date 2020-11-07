package com.panto.attendance.mapper;

import com.panto.attendance.dto.ApplicationUserResponse;
import com.panto.attendance.model.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationUserMapper {
    @Mapping(target="email",expression = "java(getEmail(user))")
    @Mapping(target="fullName",expression = "java(user.getPersonnel().getFirstName() + ' ' + user.getPersonnel().getLastName())")
    @Mapping(target="isAccountActiveAsString",expression = "java(convertStatusToString(user))")
    ApplicationUserResponse mapToDtoResponse(ApplicationUser user);

    default String convertStatusToString(ApplicationUser user){
        return user.isAccountActive() ? "Active" : "Inactive";
    }
    default String getEmail(ApplicationUser user){
        String email = user.getPersonnel().getEmail();
        return email == null ? "" : email;
    }
}
