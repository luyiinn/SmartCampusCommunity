package com.dewmark.smartcampuscommunity.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 日记实体类
 * @author dewMark
 * @create 30/11/2025
 **/
@Data
public class Diary {

    /** 主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 标题 */
    private String title;

    /** 内容（最大长度1000字符） */
    private String content;

    /** 是否公开：false-私密，true-公开 */
    private Boolean isPublic;

    /** 状态：1-发布，2-草稿，3-私有 */
    private Byte status;

    /** 浏览数 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 逻辑删除标志：false-未删除，true-已删除 */
    private Boolean isDeleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}