package com.youssef.fileshare.File;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileScanController {

    private final FileService fileService;

    // Polling endpoint
    @GetMapping("/{id}/scan-status")
    public ResponseEntity<?> getScanStatus(@PathVariable int id) {
        try {
            return ResponseEntity.ok(fileService.checkScanStatus(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
