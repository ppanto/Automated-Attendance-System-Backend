package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private Long departmentId;
    private String departmentName;
    private Long titleId;
    private String titleName;
    private Instant dateTimeJoined;
    private String dateTimeJoinedAsString;
    private String workPhone;
    private String privatePhone;
    private Date dateOfBirth;
    private String address;
    private String activeStatusAsString;
    private boolean activeStatus;
    private String gender;
}
