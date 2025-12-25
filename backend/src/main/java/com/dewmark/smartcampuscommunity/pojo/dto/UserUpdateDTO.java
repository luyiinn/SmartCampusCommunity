package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 用户更新DTO
 * @author: dewMark
 * @date: 2025/12/25
 **/
@Data
public class UserUpdateDTO implements Serializable {

    private Long id;

    private String username;

    private String avatar;

    private String email;

    private String phone;

    private Long studentId;
}