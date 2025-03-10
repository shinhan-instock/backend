package com.pda.community_module.service;

import com.pda.community_module.config.s3.AmazonS3Manager;
import com.pda.community_module.converter.FileConverter;
import com.pda.community_module.domain.File;
import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import com.pda.community_module.domain.Uuid;
import com.pda.community_module.repository.FileRepository;
import com.pda.community_module.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class S3Service {
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;
    private final FileRepository fileRepository;

    // ✅ 새로운 메서드 추가
    public String uploadFile(MultipartFile file) {
        return s3Manager.uploadFile(s3Manager.generateUserKeyName(createFileName()), file);
    }

    @Transactional
    public File setPostImage(MultipartFile file, Post post) {
        String pictureUrl = uploadFile(file); // ✅ 수정된 부분

        File newFile = File.builder()
                .url(pictureUrl)
                .user(null) // 게시글과 연결된 이미지라면 user는 null
                .post(post)
                .build();

        fileRepository.save(newFile);
        post.setFile(newFile);
        return fileRepository.save(newFile);
    }

    @Transactional
    public File setUserImage(MultipartFile file, User user) {
        String pictureUrl = uploadFile(file); // ✅ 수정된 부분

        File existingFile = fileRepository.findByUser(user);
        if (existingFile != null) {
            existingFile.setUrl(pictureUrl);
            return fileRepository.save(existingFile);
        } else {
            File newFile = File.builder()
                    .url(pictureUrl)
                    .user(user)
                    .post(null)
                    .build();

            user.setFile(newFile);
            return fileRepository.save(newFile);
        }
    }

    public Uuid createFileName() {
        String uuid = UUID.randomUUID().toString();
        return uuidRepository.save(Uuid.builder().uuid(uuid).build());
    }
}
