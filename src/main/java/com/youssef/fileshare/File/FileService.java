package com.youssef.fileshare.File;

import com.youssef.fileshare.User.User;
import com.youssef.fileshare.User.UserRepo;
import com.youssef.fileshare.storage.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserRepo userRepo;
    private final S3Service s3Service;
    private final FileRepo fileRepo;

    @Value("${aws.s3.unscannedBucket}")
    private String unscannedBucket;

    @Value("${aws.s3.cleanBucket}")
    private String cleanBucket;

    public File getFileMetadata(int id) {
        return fileRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    public File uploadFile(MultipartFile file) throws IOException {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = userDetails.getUsername();

        User user = userRepo.findByUsername(username).orElseThrow();

        String key = "users/"+ user.getId() + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Service.uploadFile(file,key);

        return fileRepo.save(File.builder()
                        .name(file.getOriginalFilename())
                        .s3key(key)
                        .size(file.getSize())
                        .contentType(file.getContentType())
                        .timestamp(Instant.now())
                        .owner(user)
                        .build());
    }

    public byte[] downloadFile(int id) {
        File file = fileRepo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
        return s3Service.downloadFile(file.getS3key());
    }

    public List<File> listUserFiles() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = userDetails.getUsername();

        User user = userRepo.findByUsername(username).orElseThrow();
        return fileRepo.findAllByOwner(user);
    }


    public Map<String, Object> checkScanStatus(int id) {
        File file = fileRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String key = file.getS3key();

        boolean inClean = s3Service.exists(cleanBucket, key);
        boolean inUnscanned = s3Service.exists(unscannedBucket, key);

        if (inClean) {
            return Map.of(
                    "id", id,
                    "status", "CLEAN"
            );
        }

        if (!inClean && !inUnscanned) {
            return Map.of(
                    "id", id,
                    "status", "INFECTED"
            );
        }

        return Map.of(
                "id", id,
                "status", "PENDING"
        );
    }


}
