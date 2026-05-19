package com.smart.community.common.core.constants.file;

import java.util.Set;

public final class FileConstants {

    /**
     * 文件不能为空
     */
    public static final String FILE_EMPTY = "文件不能为空";
    /**
     * 文件上传失败
     */
    public static final String FILE_UPLOAD_FAILED = "文件上传失败";

    /**
     * 读取文件失败
     */
    public static final String FILE_READ_FAILED = "读取文件失败";

    /**
     * 文件不存在
     */
    public static final String FILE_NOT_FOUND = "文件不存在";

    /**
     * 文件已存在
     */
    public static final String FILE_ALREADY_EXISTS = "文件已存在";

    /**
     * 不支持的文件类型
     */
    public static final String UNSUPPORTED_FILE_TYPE = "不支持的文件类型";

    /**
     * 文件大小不能超过 10MB
     */
    public static final String FILE_SIZE_EXCEEDED_10MB = "文件大小不能超过 10MB";

    /**
     * 文件最大大小 10MB
     */
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    /**
     * 允许的图片类型
     */
    public static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif","image/webp");


}
