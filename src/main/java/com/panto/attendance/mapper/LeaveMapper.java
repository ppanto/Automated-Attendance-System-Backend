package com.panto.attendance.mapper;

import com.panto.attendance.dto.LeaveInsertRequest;
import com.panto.attendance.dto.LeaveResponse;
import com.panto.attendance.dto.LeaveUpdateRequest;
import com.panto.attendance.model.Leave;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveMapper {
    @Mapping(target="personnelFullName",expression = "java(leave.getPersonnel().getFirstName() + \" \" + leave.getPersonnel().getLastName())")
    @Mapping(target="leaveTypeName",expression = "java(leave.getLeaveType().getName())")
    @Mapping(target="leaveTypeId",expression = "java(leave.getLeaveTypeId())")
    LeaveResponse mapToResponse(Leave leave);
    Leave mapToDatabase(LeaveUpdateRequest leaveUpdateRequest);
    Leave mapToDatabase(LeaveInsertRequest leaveUpdateRequest);
}
