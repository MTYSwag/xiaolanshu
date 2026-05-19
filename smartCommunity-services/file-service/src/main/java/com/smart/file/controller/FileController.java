package com.smart.file.controller;

import cn.hutool.core.util.StrUtil;
import com.smart.community.common.core.constants.file.FileConstants;
import com.smart.community.common.core.result.Result;
import com.smart.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;
    //初版
//    @PostMapping("/upload")
//    @Operation(summary = "上传文件")
//    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return Result.fail(400, FileConstants.FILE_EMPTY);
//        }
//        try {
//            String url = fileService.uploadFile(file);
//            return Result.success(url);
//        } catch (Exception e) {
//            log.error("文件上传失败：{}", e.getMessage());
//            return Result.fail(500, FileConstants.FILE_UPLOAD_FAILED);
//        }
//    }

    /**
     *为什么不提供Feign调用，因为OpenFeign 对文件上传的支持需要额外的编码器（SpringFormEncoder），
     * 配置较复杂。更简单的方式是使用 RestTemplate 或 WebClient 直接调用，
     * 或者前端直接上传到文件服务，不经过业务服务中转。
     * 在实际项目中，文件上传往往由客户端直接请求文件服务，避免经过多次网络传输。所以，
     * 我们通常不通过 Feign 传文件，而是让客户端直接调文件服务。业务服务只需要知道文件服务的地址即可。
     */

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestHeader("userId") Long userId) {
        if (file.isEmpty()) return Result.fail(400, FileConstants.FILE_EMPTY);
        try {
            String url = fileService.uploadFile(file, userId);
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败：{}，{}", userId, e.getMessage());
            return Result.fail(500, FileConstants.FILE_UPLOAD_FAILED);
        }
    }

    @GetMapping("/checkFileHash")
    @Operation(summary = "秒传检测")
    public Result<String> checkFileHash(@RequestParam String hash) {
        if (StrUtil.isNotBlank(fileService.getUrlByHash(hash))) {
            return Result.success(fileService.getUrlByHash(hash), FileConstants.FILE_ALREADY_EXISTS);
        } else {
            return Result.fail(404, FileConstants.FILE_NOT_FOUND);
        }
    }

}