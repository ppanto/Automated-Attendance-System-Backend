package com.panto.attendance.mapper;

import com.panto.attendance.dto.ShiftTypeInsertRequest;
import com.panto.attendance.model.ShiftType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.sql.Time;
import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface ShiftTypeMapper {
    @Mapping(target="startTime", expression = "java(convertTimeStampToTime(request.getStartTime()))")
    @Mapping(target="endTime", expression = "java(convertTimeStampToTime(request.getEndTime()))")
    ShiftType mapDtoToDatabase(ShiftTypeInsertRequest request);

    default Time convertTimeStampToTime(Timestamp timestamp){
        return new Time(timestamp.getTime());
    }
}
