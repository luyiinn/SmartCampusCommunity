package com.dewmark.smartcampuscommunity.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostImage {
    private Long id;           // 主键ID
    private Long postId;       // 所属帖子ID（逻辑外键）
    private String imagePath;  // 图片存储路径
    private LocalDateTime createdAt; // 创建时间（对应数据库的 TIMESTAMP）
}