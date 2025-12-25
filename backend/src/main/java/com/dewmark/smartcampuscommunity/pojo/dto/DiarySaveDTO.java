package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 日记保存DTO
 * @author: dewMark
 * @date: 2025/12/19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiarySaveDTO {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否公开：false-私密，true-公开
     */
    private Boolean isPublic;

    /**
     * 状态：1-发布，2-草稿，3-私有
     */
    private Byte status;

    /**
     * 图片列表
     **/
    private List<String> images;
}