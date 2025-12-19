package com.dewmark.smartcampuscommunity.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @description: 评论实体
 * @author: dewMark
 * @date: 2025/11/24
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    /** 评论ID，主键，自增 */
    private Long id;

    /** 所属帖子ID，关联 post 表 */
    private Long postId;

    /** 评论用户ID，关联 user 表 */
    private Long userId;

    /** 评论内容，支持长文本 */
    private String content;

    /** 是否匿名评论：0-否，1-是 */
    private Integer isAnonymous;

    /** 回复的评论ID，用于构建评论树（嵌套回复），为 null 表示一级评论 */
    private Long replyCommentId;

    /** 被回复用户的ID，当 reply_comment_id 不为空时该字段有效 */
    private Long replyUserId;

    /** 评论创建时间 */
    private LocalDateTime createAt;

    /** 评论点赞数，默认为0 */
    private Integer likeCount;

    /** 评论最后更新时间 */
    private LocalDateTime updateAt;

    /** 逻辑删除标识：0-未删除，1-已删除 */
    private Integer isDeleted;
}
