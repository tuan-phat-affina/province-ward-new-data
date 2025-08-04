package com.example.crawl_province_ward.repository;

import com.example.crawl_province_ward.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WardRepository extends JpaRepository<Ward,Long> {
}
