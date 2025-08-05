package com.example.crawl_province_ward.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OldProvince {
    @Id
    private Long id;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OldDistrict> districts = new ArrayList<>();
}