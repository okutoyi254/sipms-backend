package com.sipms.logistics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable{

    private static final long serialVersionID=1L;

    @Column(name = "street",length = 200)
    private String street;

    @Column(name = "city",length = 100)
    private String city;

    @Column(name = "state",length = 100)
    private String state;

    @Column(name = "postal_code",length = 20)
    private String postalCode;

    @Column(name = "country",length = 100)
    private String country;


}
