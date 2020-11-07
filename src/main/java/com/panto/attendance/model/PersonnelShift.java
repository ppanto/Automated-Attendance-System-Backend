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
public class PersonnelShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private ShiftType shiftType;
    @Column(name="shift_type_id", insertable = false, updatable = false)
    private Long shiftTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Personnel personnel;
    @Column(name="personnel_id", insertable = false, updatable = false)
    private Long personnelId;
    private Date startDate;
    private Date endDate;
}
