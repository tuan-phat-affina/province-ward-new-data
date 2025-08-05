package com.example.crawl_province_ward.controller;

import com.example.crawl_province_ward.service.WardCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlerController {

    private final WardCrawlerService wardCrawlerService;

    @PostMapping
    public ResponseEntity<String> crawlWards() {
        try {
            for (int i = 1; i <= 34; i++) {
                wardCrawlerService.crawlWardDataById(i);
                Thread.sleep(100); // để tránh bị block IP
            }
            return ResponseEntity.ok("Crawl và lưu thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/old-locations")
    public ResponseEntity<String> crawlOldLocations() {
        try {
            wardCrawlerService.fetchAndSaveOldLocations();
            return ResponseEntity.ok("Crawl dữ liệu tỉnh/huyện/xã cũ thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi crawl dữ liệu: " + e.getMessage());
        }
    }
}

