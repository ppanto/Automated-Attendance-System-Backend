package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUserUpdateRequest {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
