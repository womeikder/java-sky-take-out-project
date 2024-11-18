package com.sky.controller.admin;

import com.sky.config.OssConfiguration;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通过接口")
public class CommonController {
    @Autowired
    public AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传{}",file);
        try {
            // 获取文件名，截取后缀，通过UUID拼接为新的文件名
            String filename = file.getOriginalFilename();
            String substring = filename.substring(filename.lastIndexOf('.'));
            String newName = UUID.randomUUID().toString() + substring;
            // 上传至阿里云
            String upload = aliOssUtil.upload(file.getBytes(), newName);
            return Result.success(upload);
        } catch (IOException e) {
            log.info("文件上传失败{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
