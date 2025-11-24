package com.dewmark.smartcampuscommunity.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.exception.BaseException;
import com.dewmark.smartcampuscommunity.mapper.PostMapper;
import com.dewmark.smartcampuscommunity.mapper.PostTagMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.PostTag;
import com.dewmark.smartcampuscommunity.pojo.entity.Tag;
import com.dewmark.smartcampuscommunity.service.PostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/16
 **/
@Service
public class PostTagServiceImpl extends ServiceImpl<PostTagMapper, PostTag> implements PostTagService {

}
