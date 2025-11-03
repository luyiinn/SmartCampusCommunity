package com.dewmark.smartcampuscommunity.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/03
 **/
@Data
public class UsersDTO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
