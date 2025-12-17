package com.dewmark.smartcampuscommunity.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVO<T> {
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 数据列表
     */
    private List<T> list;
}