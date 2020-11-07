package com.panto.attendance.mapper;

import com.panto.attendance.dto.PersonnelExtraSimpleResponse;
import com.panto.attendance.dto.PersonnelResponse;
import com.panto.attendance.dto.PersonnelSimpleResponse;
import com.panto.attendance.dto.PersonnelUpsertRequest;
import com.panto.attendance.model.Department;
import com.panto.attendance.model.Personnel;
import com.panto.attendance.model.Title;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring")
public interface PersonnelMapper {
    @Mapping(target="fullName",expression = "java(personnel.getFirstName() + ' ' + personnel.getLastName())")
    @Mapping(target="departmentId",expression = "java(getDepartmentId(personnel))")
    @Mapping(target="departmentName",expression = "java(getDepartmentName(personnel))")
    @Mapping(target="titleId",expression = "java(getTitleId(personnel))")
    @Mapping(target="titleName",expression = "java(getTitleName(personnel))")
    @Mapping(target="dateTimeJoinedAsString",expression = "java(convertInstantToString(personnel.getDateTimeJoined()))")
    @Mapping(target="activeStatusAsString",expression = "java(convertStatusToString(personnel.isActiveStatus()))")
    PersonnelResponse mapToDtoResponse(Personnel personnel);

    default Long getTitleId(Personnel personnel){
        Title title = personnel.getTitle();
        if(title == null) return 0L;
        return title.getId();
    }
    default String getTitleName(Personnel personnel){
        Title title = personnel.getTitle();
        if(title == null) return "";
        return title.getName();
    }
    default Long getDepartmentId(Personnel personnel){
        Department department = personnel.getDepartment();
        if(department == null) return 0L;
        return department.getId();
    }
    default String getDepartmentName(Personnel personnel){
        Department department = personnel.getDepartment();
        if(department == null) return "";
        return department.getName();
    }
    default String convertInstantToString(Instant instant){
        if(instant == null) return "";
        DateTimeFormatter formatter =
                DateTimeFormatter
                        .ofPattern("yyyy/MM/dd")
                        .withZone( ZoneId.of("Europe/Belgrade") );
        return formatter.format(instant);
    }
    default String convertStatusToString(boolean status){
        return status ? "Active" : "Inactive";
    }

    @Mapping(target="id", ignore = true)
    //@Mapping(target="image", ignore = true)
    @Mapping(target="title",source = "title")
    @Mapping(target="department", source = "department")
    Personnel mapToDatabasePersonnel(PersonnelUpsertRequest entity, Title title, Department department);

    @Mapping(target="fullName",expression = "java(personnel.getFirstName() + ' ' + personnel.getLastName())")
    PersonnelSimpleResponse mapToDtoSimpleResponse(Personnel personnel);

    @Mapping(target="fullName",expression = "java(personnel.getFirstName() + ' ' + personnel.getLastName())")
    @Mapping(target="department",expression = "java(personnel.getDepartment().getName())")
    @Mapping(target="title",expression = "java(personnel.getTitle().getName())")
    PersonnelExtraSimpleResponse mapToDtoExtraSimpleResponse(Personnel personnel);
}

