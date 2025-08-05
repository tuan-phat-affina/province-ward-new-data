package com.example.crawl_province_ward.service;

import com.example.crawl_province_ward.entity.*;
import com.example.crawl_province_ward.repository.OldProvinceRepository;
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

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class WardCrawlerService {

    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final OldProvinceRepository oldProvinceRepository;
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

    public void fetchAndSaveOldLocations() {
        String url = "https://esgoo.net/api-tinhthanh/4/0.htm";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Không nhận được dữ liệu từ API");
        }

        List<Map<String, Object>> provinces = (List<Map<String, Object>>) response.get("data");

        for (Map<String, Object> p : provinces) {
            OldProvince province = new OldProvince();
            province.setId(Long.valueOf((String) p.get("id")));
            province.setName((String) p.get("name"));
            province.setNameEn((String) p.get("name_en"));
            province.setFullName((String) p.get("full_name"));
            province.setFullNameEn((String) p.get("full_name_en"));
            province.setLatitude(parseDoubleSafe(p.get("latitude")));
            province.setLongitude(parseDoubleSafe(p.get("longitude")));

            List<Map<String, Object>> districts = (List<Map<String, Object>>) p.get("data2");
            if (districts != null) {
                for (Map<String, Object> d : districts) {
                    OldDistrict district = new OldDistrict();
                    district.setId(Long.valueOf((String) d.get("id")));
                    district.setName((String) d.get("name"));
                    district.setNameEn((String) d.get("name_en"));
                    district.setFullName((String) d.get("full_name"));
                    district.setFullNameEn((String) d.get("full_name_en"));
                    district.setLatitude(parseDoubleSafe(d.get("latitude")));
                    district.setLongitude(parseDoubleSafe(d.get("longitude")));
                    district.setProvince(province);

                    List<Map<String, Object>> wards = (List<Map<String, Object>>) d.get("data3");
                    if (wards != null) {
                        for (Map<String, Object> w : wards) {
                            OldWard ward = new OldWard();
                            ward.setId(Long.valueOf((String) w.get("id")));
                            ward.setName((String) w.get("name"));
                            ward.setNameEn((String) w.get("name_en"));
                            ward.setFullName((String) w.get("full_name"));
                            ward.setFullNameEn((String) w.get("full_name_en"));
                            ward.setLatitude(parseDoubleSafe(w.get("latitude")));
                            ward.setLongitude(parseDoubleSafe(w.get("longitude")));
                            ward.setDistrict(district);
                            district.getWards().add(ward);
                        }
                    }

                    province.getDistricts().add(district);
                }
            }

            oldProvinceRepository.save(province);
        }
    }

    private Double parseDoubleSafe(Object val) {
        if (val == null) return null;
        try {
            return Double.valueOf(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
