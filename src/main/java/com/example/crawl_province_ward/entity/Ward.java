package com.example.crawl_province_ward.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String type;
    private Double areaKm2;
    private Long population;
    private String center;
    private Double latitude;
    private Double longitude;

    @Column(length = 2048)
    private String beforeMerge;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;
}
