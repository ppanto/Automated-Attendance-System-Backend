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
}
