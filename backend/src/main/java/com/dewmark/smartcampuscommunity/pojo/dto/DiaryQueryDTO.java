package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.Data;

/**
 * @description: 日记分页查询
 * @author: dewMark
 * @date: 2025/12/08
 **/
@Data
public class DiaryQueryDTO {

    /**
     * 搜索关键词（标题或内容）
     */
    private String keyword;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer size;

    /**
     * 用户ID（可选，用于查询特定用户的日记）
     */
    private Long userId;

    /**
     * 是否仅查询公开日记（可选，true: 仅公开，false: 全部）
     */
    private Boolean isPublic;

}
