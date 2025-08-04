package com.example.crawl_province_ward.service;

import com.example.crawl_province_ward.entity.Province;
import com.example.crawl_province_ward.entity.Ward;
import com.example.crawl_province_ward.entity.WardResponseDTO;
import com.example.crawl_province_ward.repository.ProvinceRepository;
import com.example.crawl_province_ward.repository.WardRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class WardCrawlerService {

    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final RestTemplate restTemplate;

    private final String BASE_URL = "https://sapnhap.bando.com.vn/ptracuu";

    public void crawlWardDataById(int id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("id", String.valueOf(id));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            for (JsonNode node : root) {
                // Chỉ lấy các trường theo tên (bỏ qua "0", "1", ...)
                String code = getTextSafely(node, "ma");
                String name = getTextSafely(node, "tenhc");
                String type = getTextSafely(node, "loai");
                String provinceCode = getTextSafely(node, "matinh");
                String provinceName = getTextSafely(node, "tentinh");

                double areaKm2 = getDoubleSafely(node, "dientichkm2");
                long population = getLongSafely(node, "dansonguoi");
                String center = getTextSafely(node, "trungtamhc");
                double latitude = getDoubleSafely(node, "vido");
                double longitude = getDoubleSafely(node, "kinhdo");
                String beforeMerge = getTextSafely(node, "truocsapnhap");

                Province province = provinceRepository
                        .findByCode(provinceCode)
                        .orElseGet(() -> provinceRepository.save(new Province(provinceCode, provinceName)));

                Ward ward = new Ward();
                ward.setCode(code);
                ward.setName(name);
                ward.setType(type);
                ward.setAreaKm2(areaKm2);
                ward.setPopulation(population);
                ward.setCenter(center);
                ward.setLatitude(latitude);
                ward.setLongitude(longitude);
                ward.setBeforeMerge(beforeMerge);
                ward.setProvince(province);

                wardRepository.save(ward);
            }

        } catch (Exception e) {
            System.err.println("Error with id=" + id + ": " + e.getMessage());
        }
    }

    private String getTextSafely(JsonNode node, String key) {
        JsonNode val = node.get(key);
        return val != null ? val.asText() : "";
    }

    private double getDoubleSafely(JsonNode node, String key) {
        JsonNode val = node.get(key);
        return val != null && val.isNumber() ? val.asDouble() : 0.0;
    }

    private long getLongSafely(JsonNode node, String key) {
        JsonNode val = node.get(key);
        try {
            return val != null ? Long.parseLong(val.asText().replaceAll("[^\\d]", "")) : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
