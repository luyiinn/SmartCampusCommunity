package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.Data;

@Data
public class PostQueryDTO {
    /**
     * 搜索关键词（标题或内容）
     */
    private String keyword;
    
    /**
     * 标签ID
     */
    private Long tagId;
    
    /**
     * 页码
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer size;
    
    /**
     * 用户ID（可选，用于查询特定用户的帖子）
     */
    private Long userId;

    /**
     * 状态（可选，用于筛选特定状态的帖子）
     */
    private Integer status;
}