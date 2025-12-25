package com.dewmark.smartcampuscommunity.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 日记列表查询响应
 * @author: dewMark
 * @date: 2025/12/08
 **/
@Data
public class DiaryListVO {

    /**
     * 日记ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容摘要
     */
    private String contentSummary;

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
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 当前用户是否点赞
     */
    private Byte isLike;

    /**
     * 图片列表
     **/
    private  List<String> images;

}
