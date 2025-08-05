package com.example.crawl_province_ward.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OldDistrict {
    @Id
    private Long id;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private OldProvince province;

    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OldWard> wards = new ArrayList<>();
}