package com.panto.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Data
public class Personnel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @ManyToOne(fetch = LAZY)
    private Department department;
    @ManyToOne(fetch = LAZY)
    private Title title;
    private Instant dateTimeJoined;
    private String workPhone;
    private String privatePhone;
    private Date dateOfBirth;
    private String address;
    @NotNull
    private boolean activeStatus;
    private String gender;
}
