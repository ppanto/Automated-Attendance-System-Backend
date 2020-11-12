package com.panto.attendance.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceActionSimpleResponse {
    private Long personnelId;
    private String personnelName;
    private LocalDateTime dateTime;
    private String event;
    private Long eventId;
    private Long id;

    private int messageCode = 0;

    public AttendanceActionSimpleResponse(Long personnelId, String personnelName, LocalDateTime dateTime,
                                          String event, Long eventId, Long id){
        this.personnelId = personnelId;
        this.personnelName = personnelName;
        this.dateTime = dateTime;
        this.event = event;
        this.eventId = eventId;
        this.id = id;
    }
}
