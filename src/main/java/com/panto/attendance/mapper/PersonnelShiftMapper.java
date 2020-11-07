package com.panto.attendance.mapper;

import com.panto.attendance.dto.PersonnelShiftResponse;
import com.panto.attendance.dto.PersonnelShiftUpsertRequest;
import com.panto.attendance.model.PersonnelShift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonnelShiftMapper {
    @Mapping(target="personnelFullName",expression = "java(personnelShift.getPersonnel().getFirstName() + \" \" + personnelShift.getPersonnel().getLastName())")
    @Mapping(target="startTime",expression = "java(personnelShift.getShiftType().getStartTime())")
    @Mapping(target="endTime",expression = "java(personnelShift.getShiftType().getEndTime())")
    @Mapping(target="shiftTypeName",expression = "java(personnelShift.getShiftType().getName())")
    @Mapping(target="shiftTypeId",expression = "java(personnelShift.getShiftTypeId())")
    PersonnelShiftResponse mapToResponse(PersonnelShift personnelShift);
    PersonnelShift mapToDatabase(PersonnelShiftUpsertRequest personnelShiftUpsertRequest);
}
