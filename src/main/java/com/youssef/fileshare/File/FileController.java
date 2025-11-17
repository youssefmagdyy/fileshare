package com.youssef.fileshare.File;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int id) {
        File file = fileService.getFileMetadata(id);
        byte[] data = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(data);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file) throws IOException {
        try {
            File saved = fileService.uploadFile(file);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping
    public ResponseEntity<List<File>> listFiles() {
        return ResponseEntity.ok(fileService.listUserFiles());
    }

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
