package com.example.crawl_province_ward.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WardResponseDTO {
    private int id;
    private int matinh;
    private String ma;
    private String tentinh;
    private String loai;
    private String tenhc;
    private double dientichkm2;
    private String dansonguoi;
    private String trungtamhc;
    private double kinhdo;
    private double vido;
    private String truocsapnhap;
}
