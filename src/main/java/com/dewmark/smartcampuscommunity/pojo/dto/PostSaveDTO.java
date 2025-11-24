package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/16
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSaveDTO {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否匿名：0-不匿名，1-匿名
     */
    private Integer isAnonymous;

    /**
     * 状态：1-发布，2-草稿，3-私有
     */
    private Integer status;

    /**
     * 标签列表
     **/
    private List<Long> tags;
}
