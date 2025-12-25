package com.dewmark.smartcampuscommunity.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 用户信息VO
 * @author: dewMark
 * @date: 2025/12/25
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO implements Serializable {

    private Long id;

    private String username;

    private String avatar;

    private String email;

    private String phone;

    private Long studentId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}