package com.dewmark.smartcampuscommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.exception.BaseException;
import com.dewmark.smartcampuscommunity.mapper.TagMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.Tag;
import com.dewmark.smartcampuscommunity.service.TagService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/16
 **/
@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public List<Tag> selectList() {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("use_count");
        List<Tag> tags = tagMapper.selectList(wrapper);
        if (tags == null || tags.size() == 0) {
            throw new BaseException(MessageConstant.DATABASE_OPRATE);
        }
        return tags;
    }
}
