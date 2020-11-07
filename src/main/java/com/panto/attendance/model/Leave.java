package com.panto.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.sql.Date;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Data
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = LAZY)
    private Personnel personnel;
    @Column(name = "personnel_id", insertable = false, updatable = false)
    private Long personnelId;
    private Date date;
    private String description;
    private boolean approved;
    //private boolean isPaid;
    @ManyToOne(fetch = LAZY)
    private LeaveType leaveType;
    @Column(name = "leave_type_id", insertable = false, updatable = false)
    private Long leaveTypeId;
}
