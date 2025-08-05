package com.example.crawl_province_ward.repository;

import com.example.crawl_province_ward.entity.OldProvince;
import com.example.crawl_province_ward.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OldProvinceRepository extends JpaRepository<OldProvince,Long> {
}
