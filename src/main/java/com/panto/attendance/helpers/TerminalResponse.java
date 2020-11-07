package com.panto.attendance.helpers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TerminalResponse {
    public boolean isSuccessful;
    public String message;
    public String fullName;
}
