package com.panto.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUserInsertRequest {
    @NotBlank
    private Long id;
    @NotBlank
    private String password;
    @NotBlank
    private Long personnelId;
    @NotBlank
    private String username;
}
