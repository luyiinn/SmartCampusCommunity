package com.dewmark.smartcampuscommunity.pojo.entity;

import lombok.Data;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/16
 **/
@Data
public class Tag {
    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 使用次数
     */
    private Integer useCount;
}
