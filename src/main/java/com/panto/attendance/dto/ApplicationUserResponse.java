package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private boolean isAccountActive;
    private String isAccountActiveAsString;
    private Long personnelId;
}
