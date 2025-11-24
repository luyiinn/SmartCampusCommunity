package com.dewmark.smartcampuscommunity.pojo.entity;

import lombok.Data;

/**
 * @description: 帖子标签关系表标签
 * @author: dewMark
 * @date: 2025/11/16
 **/
@Data
public class PostTag {

    private Long id;

    private Long tagId;

    private Long postId;


}
