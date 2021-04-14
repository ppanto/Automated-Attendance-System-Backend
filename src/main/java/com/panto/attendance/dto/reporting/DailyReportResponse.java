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
    private int present = 0; // were or are at work at given date
    private int absent = 0; // were not at work at all at given date

    private int checkedIn = 0; // have check in but no check out on given date
    private int yetToCheckIn = 0; // no leave so should check in but not checked yet
    private int checkedOut = 0; // have at least one check out

    private int approvedLeave = 0; // have logged leave
    private int unapprovedLeave = 0; // have logged leave but not approved

    private Collection<PersonnelExtraSimpleResponse> currentlyAtWork;
    private List<PersonnelExtraSimpleResponse> currentlyNotAtWork;
}
