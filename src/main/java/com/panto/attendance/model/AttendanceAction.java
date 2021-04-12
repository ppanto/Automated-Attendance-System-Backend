package com.panto.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Data
public class AttendanceAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private Instant dateTime;
    private Timestamp dateTime;
    @Column(name = "event_date")
    private Date date;
    @ManyToOne(fetch = EAGER)
    private AttendanceEvent attendanceEvent;
    @ManyToOne(fetch = LAZY)
    private Personnel personnel;
    @Column(name = "personnel_id", insertable = false, updatable = false)
    private Long personnelId;

//    public AttendanceAction(Timestamp dateTime, AttendanceEvent attendanceEvent, Personnel personnel, Date date){
//        this.dateTime = dateTime;
//        this.attendanceEvent = attendanceEvent;
//        this.personnel = personnel;
//        this.date = date;
//    }
    public AttendanceAction(Timestamp dateTime, Personnel personnel, Date date){
        this.dateTime = dateTime;
        this.personnel = personnel;
        this.date = date;
    }
}
