package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelUpsertRequest {
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String email;
    private Long departmentId;
    private String image;
    private Long titleId;
    private Instant dateTimeJoined;
    private String workPhone;
    private String privatePhone;
    private Date dateOfBirth;
    private String address;
    private boolean activeStatus;
    private String gender;
}
