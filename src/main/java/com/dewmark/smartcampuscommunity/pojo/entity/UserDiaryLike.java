package com.dewmark.smartcampuscommunity.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDiaryLike {

    private Long id;          // 主键 ID（对应自增主键）

    private Long userId;      // 用户 ID

    private Long diaryId;     // 日志 ID

    private Byte likeStatus;  // 点赞状态：1-点赞，0-取消点赞
}