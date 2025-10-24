package com.youssef.fileshare.File;

import com.youssef.fileshare.User.User;
import com.youssef.fileshare.User.UserRepo;
import com.youssef.fileshare.storage.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserRepo userRepo;
    private final S3Service s3Service;
    private final FileRepo fileRepo;

    public File getFileMetadata(int id) {
        return fileRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    public File uploadFile(MultipartFile file) throws IOException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepo.findByUsername(username).orElseThrow();
        return fileRepo.FindAllByOwner(user);
    }

}
