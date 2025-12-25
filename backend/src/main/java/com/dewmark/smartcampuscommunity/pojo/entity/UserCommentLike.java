package com.dewmark.smartcampuscommunity.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户评论点赞关系表实体类
 * 
 * @author dewMark
 * @date 2025-12-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_comment_likes")
public class UserCommentLike {

    /**
     * 自增主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 点赞状态：1-点赞，0-取消点赞
     */
    private Integer likeStatus;
}