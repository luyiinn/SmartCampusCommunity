package com.dewmark.smartcampuscommunity.pojo.vo;

import com.dewmark.smartcampuscommunity.pojo.entity.Tag;
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
public class TagListVO {
    private List<Tag> tags;
}
