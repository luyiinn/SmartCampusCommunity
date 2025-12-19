package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @description: 注册DTO
 * @author: dewMark
 * @date: 2025/11/15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersRegisterDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     **/
    private String email;

    /**
     * 手机号
     **/
    private String phone;

    /**
     * 学号
     **/

    private Long studentId;

}
