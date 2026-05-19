package com.smart.file.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.community.common.core.constants.file.FileConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.file.entity.TFile;
import com.smart.file.mapper.TFileMapper;
import com.smart.file.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.prefs.BackingStoreException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl extends ServiceImpl<TFileMapper, TFile> implements FileService {

//    /**
//     * 上传文件到 MinIO，返回完整的可访问 URL(初版)
//     */
//    @Override
//    public String uploadFile(MultipartFile file){
//        String originalFilename = file.getOriginalFilename();
//        String extension = "";
//        if (originalFilename != null && originalFilename.contains(".")) {
//            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        }
//        // 生成唯一文件名，防止覆盖
//        String objectName = UUID.randomUUID().toString(true) + extension;
//
//        try {
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(objectName)
//                            .stream(file.getInputStream(), file.getSize(), -1)
//                            .contentType(file.getContentType())
//                            .build()
//            );
//            return endpoint + "/" + bucketName + "/" + objectName;
//        } catch (IOException e) {
//            log.error("读取文件流失败，文件名: {}", originalFilename, e);
//            throw new BusinessException(FileConstants.FILE_READ_FAILED);
//        } catch (Exception e) {
//            log.error("MinIO 上传失败，文件名: {}", originalFilename, e);
//            throw new BusinessException(FileConstants.FILE_UPLOAD_FAILED);
//        }
//    }
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件（自动去重，返回已有的 URL 或新的 URL）
     * @param file MultipartFile
     * @param userId 上传者ID
     * @return 文件访问 URL
     */
    @Override
    public String uploadFile(MultipartFile file, Long userId) {
        // 1. 校验文件类型和大小（仅限图片类，可根据需要扩展）
        String contentType = file.getContentType();
        if (StrUtil.isEmpty(contentType) || !FileConstants.ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new RuntimeException(FileConstants.UNSUPPORTED_FILE_TYPE + "：" + contentType);
        }
        if (file.getSize() > FileConstants.MAX_FILE_SIZE) {
            throw new RuntimeException(FileConstants.FILE_SIZE_EXCEEDED_10MB);
        }

        // 2. 计算文件 MD5
        String md5;
        try {
           InputStream input = file.getInputStream();
            md5 = DigestUtil.md5Hex(input);
        } catch (IOException e) {
            throw new BusinessException(FileConstants.FILE_READ_FAILED);
        }
        // 查询是否已有相同 Hash 的文件
        TFile existFile = this.getOne(
                new LambdaQueryWrapper<TFile>().eq(TFile::getFileHash, md5));
        if (Objects.nonNull(existFile)) {
            // 秒传：直接返回已有 URL
            return existFile.getFileUrl();
        }

        // 3. 生成唯一对象名
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String objectName = UUID.randomUUID().toString(true) + extension;

        // 4. 上传到 MinIO（需要重新获取 InputStream，因为 MD5 计算已经消耗了流）
        try {
            InputStream uploadStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(uploadStream, file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );
        } catch (Exception e){
            log.error("MinIO 上传失败，文件名: {}", originalName, e);
            throw new BusinessException(FileConstants.FILE_UPLOAD_FAILED);
        }

        // 5. 生成 URL 并保存数据库
        String fileUrl = endpoint + "/" + bucketName + "/" + objectName;
        TFile record = new TFile();
        record.setUserId(userId);
        record.setOriginalName(originalName);
        record.setObjectName(objectName);
        record.setFileUrl(fileUrl);
        record.setFileSize(file.getSize());
        record.setContentType(contentType);
        record.setFileHash(md5);
        this.save(record);

        return fileUrl;
    }

    /**
     * 根据 Hash 检查文件是否已存在（供其他服务调用）
     * @return 文件 URL，若不存在返回 null
     */
    public String getUrlByHash(String hash) {
        TFile record = this.getOne(
                new LambdaQueryWrapper<TFile>().eq(TFile::getFileHash, hash));
        return Objects.nonNull(record) ? record.getFileUrl() : null;
    }

}
