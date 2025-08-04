package com.example.crawl_province_ward.repository;

import com.example.crawl_province_ward.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province,Long> {
    Optional<Province> findByCode(String code);
}
