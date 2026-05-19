package com.smart.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {
//    /**
//     * 上传文件（初版：返回完整的可访问 URL）
//     * @param file 文件
//     * @return 文件URL
//     */
//       String uploadFile(MultipartFile file);

    /**
     * 上传文件（需用户ID）
     * @param file 文件
     * @param userId 用户ID
     * @return 文件URL
     */
    String uploadFile(MultipartFile file, Long userId);

    /**
     * 根据文件哈希值获取文件URL
     * @param hash 文件哈希值
     * @return 文件URL
     */
    String getUrlByHash(String hash);
}
