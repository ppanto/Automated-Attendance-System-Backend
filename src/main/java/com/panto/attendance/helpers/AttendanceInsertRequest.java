package com.panto.attendance.helpers;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class AttendanceInsertRequest {
    @NotNull
    public Long personnelId;
    @NotNull
    public int buttonId;
    public Timestamp timeStamp = null;
    public Long eventId = null;
}
