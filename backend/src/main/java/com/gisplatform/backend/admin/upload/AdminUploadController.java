package com.gisplatform.backend.admin.upload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/uploads")
public class AdminUploadController {

    private final CsvUploadService csvUploadService;

    public AdminUploadController(CsvUploadService csvUploadService) {
        this.csvUploadService = csvUploadService;
    }

    @PostMapping("/facilities")
    public ResponseEntity<CsvUploadResponse> uploadFacilities(@RequestPart("file") MultipartFile file) {
        return response(csvUploadService.uploadFacilities(file));
    }

    @PostMapping("/competitors")
    public ResponseEntity<CsvUploadResponse> uploadCompetitors(@RequestPart("file") MultipartFile file) {
        return response(csvUploadService.uploadCompetitors(file));
    }

    @PostMapping("/transit-stops")
    public ResponseEntity<CsvUploadResponse> uploadTransitStops(@RequestPart("file") MultipartFile file) {
        return response(csvUploadService.uploadTransitStops(file));
    }

    @PostMapping("/foot-traffic")
    public ResponseEntity<CsvUploadResponse> uploadFootTraffic(@RequestPart("file") MultipartFile file) {
        return response(csvUploadService.uploadFootTraffic(file));
    }

    private static ResponseEntity<CsvUploadResponse> response(CsvUploadResponse response) {
        HttpStatus status = response.hasErrors() ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
