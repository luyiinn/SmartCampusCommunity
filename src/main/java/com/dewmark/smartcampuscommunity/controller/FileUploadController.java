package com.dewmark.smartcampuscommunity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    // 上传目录（相对路径，项目根目录下的 uploads 文件夹）
    private static final String UPLOAD_FOLDER = "uploads/";

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        // 1. 检查文件是否为空
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "文件为空");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 2. 创建上传目录（如果不存在）
            Path uploadPath = Paths.get(UPLOAD_FOLDER);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. 安全处理文件名（防止路径穿越攻击）
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.contains("..")) {
                response.put("success", false);
                response.put("message", "非法文件名");
                return ResponseEntity.badRequest().body(response);
            }

            // 4. 生成唯一文件名（避免覆盖）
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            String safeFileName = UUID.randomUUID() + extension;

            // 5. 保存文件到本地
            Path filePath = uploadPath.resolve(safeFileName);
            Files.copy(file.getInputStream(), filePath);

            // 6. 返回成功响应
            response.put("success", true);
            response.put("message", "上传成功");
            response.put("fileName", safeFileName);
            response.put("url", "/uploads/" + safeFileName); // 可通过此 URL 访问

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "上传失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}