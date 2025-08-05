package com.example.crawl_province_ward.entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OldLocationResponse {
    private int error;
    private String error_text;
    private List<OldProvince> data;
}
