package com.panto.attendance.dto.reporting;

import com.panto.attendance.dto.PersonnelExtraSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyReportResponse {
    private int totalPersonnel;
    private int present = 0;
    private int absent = 0;
    private int approvedLeave = 0;
    private int unapprovedLeave = 0;
    private int checkedIn = 0;
    private int yetToCheckIn = 0;
    private int checkedOut = 0;
    private Collection<PersonnelExtraSimpleResponse> currentlyAtWork;
    private List<PersonnelExtraSimpleResponse> currentlyNotAtWork;
}
