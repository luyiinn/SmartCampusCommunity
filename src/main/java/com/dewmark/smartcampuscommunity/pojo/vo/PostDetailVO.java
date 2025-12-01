package com.dewmark.smartcampuscommunity.pojo.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 帖子详情
 * @author: dewMark
 * @date: 2025/12/01
 **/
public class PostDetailVO {
    /**
     * 帖子ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否匿名
     */
    private Integer isAnonymous;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 当前用户是否点赞
     */
    private Integer isLike;

    /**
     * 图片列表
     **/
    private  List<String> images;
}
