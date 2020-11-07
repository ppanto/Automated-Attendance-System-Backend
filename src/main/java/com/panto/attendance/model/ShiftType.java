package com.panto.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.sql.Time;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Data
public class ShiftType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Time startTime;
    @NotNull
    private Time endTime;
    private boolean active = true;
}
