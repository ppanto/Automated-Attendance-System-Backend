package com.panto.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Table
@Data
public class PersonnelImage {
    @Id
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Personnel personnel;
    @Lob
    private byte[] image;

    public PersonnelImage(Long personnelId, Personnel personnel, byte[] image){
        this.id = personnelId;
        this.image = image;
        this.personnel = personnel;
    }
}
