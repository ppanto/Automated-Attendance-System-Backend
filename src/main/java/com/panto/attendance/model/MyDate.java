package com.panto.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Data
public class MyDate {
    @Id
    private Date fullDate;
    private short dayInWeekNumber;
    private short monthNumber;
    private short yearNumber;
    private boolean isHoliday;
    private boolean isWeekend;
}
