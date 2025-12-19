package com.dewmark.smartcampuscommunity.pojo.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostQueryBO {
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 标签ID
     */
    private Long tagId;
    
    /**
     * 起始索引
     */
    private Integer offset;
    
    /**
     * 每页数量
     */
    private Integer limit;
    
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 状态
     */
    private Integer status;
}