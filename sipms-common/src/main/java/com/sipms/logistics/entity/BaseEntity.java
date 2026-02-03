package com.sipms.logistics.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",updatable = false,nullable = false)
    private Long id;

    @CreatedDate
    @Column(name="created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by",updatable = false,length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by",length = 100)
    private String updatedBy;

    @Column(name = "active",nullable = false)
    private Boolean active= true;


    public void safeDelete(){
        this.active=false;
    }

    public void restore(){
        this.active=true;
    }

    public boolean isNew(){
        return this.id==null;
    }
}
