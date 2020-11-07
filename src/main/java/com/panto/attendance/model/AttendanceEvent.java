package com.panto.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Data
public class AttendanceEvent {
    //    1	WorkStart
    //    2	BreakStart
    //    3	OfficialStart
    //    4	WorkEnd
    //    5	BreakEnd
    //    6	OfficialEnd
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
}
