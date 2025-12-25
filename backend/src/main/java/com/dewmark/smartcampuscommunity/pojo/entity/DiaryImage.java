package com.dewmark.smartcampuscommunity.pojo.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class DiaryImage {

    private Long id;           // 主键ID

    private Long diaryId;      // 所属日记ID（没有外键约束）

    private String imagePath;  // 图片存储路径

    private Timestamp createdAt; // 创建时间（可为空，由后端赋值）
}