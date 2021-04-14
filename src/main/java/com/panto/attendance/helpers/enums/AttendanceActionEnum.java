package com.panto.attendance.helpers.enums;

public enum AttendanceActionEnum{
    WORK_START (1L),
    BREAK_START (2L),
    OFFICIAL_START (3L),
    WORK_END (4L),
    BREAK_END (5L),
    OFFICIAL_END (6L);

    private final Long actionId;
    private AttendanceActionEnum(Long actionId){
        this.actionId = actionId;
    }
    public Long getValue(){
        return this.actionId;
    }
}