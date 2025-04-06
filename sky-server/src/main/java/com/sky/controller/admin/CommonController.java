package com.sky.controller.admin;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 该控制器为公共的接口
 *
 * @author aoao
 * @create 2025-04-05-18:07
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("upload")
    @ApiOperation("上传文件")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}",file);
        //获取文件旧的名称
        String originalFilename = file.getOriginalFilename();
        //获取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        try {
            //获取文件位置
            String filePath = aliOssUtil.upload(file.getBytes(), UUID.randomUUID().toString() + suffix);
            return Result.success(filePath);
        } catch (IOException e) {
            throw new BaseException();
        }
    }
}
