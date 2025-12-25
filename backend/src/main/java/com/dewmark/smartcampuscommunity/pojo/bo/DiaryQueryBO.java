package com.dewmark.smartcampuscommunity.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/12/08
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryQueryBO {
    /**
     * 搜索关键词
     */
    private String keyword;

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
     * 是否本人
     */
    private Integer isOneSelf;

    /**
     * 是否仅查询公开日记
     */
    private Boolean isPublic;

}
