package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonnelExtraSimpleResponse {
    private Long id;
    private String fullName;
    private String department;
    private String title;
}
