package com.dewmark.smartcampuscommunity.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/15
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO implements Serializable {

    private Long id;

    private String userName;

    private String avatar;

    private String token;
}
