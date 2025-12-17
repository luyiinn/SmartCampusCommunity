package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.Data;

/**
 * @description: CommentDTO
 * @author: dewMark
 * @date: 2025/11/24
 **/
@Data
public class CommentDTO {

    /**
     * 帖子id
     **/
    private Long postId;

    /**
     * 评论内容
     **/
    private String content;

    /**
     * 回复人id
     **/
    private Long replyUserId;

    /**
     * 回复评论id
     **/
    private Long replyCommentId;

    /**
     * 是否匿名
     **/
    private Integer isAnonymous;
}
