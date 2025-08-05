package com.example.crawl_province_ward.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OldWard {
    @Id
    private Long id;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private OldDistrict district;
}
