package com.dewmark.smartcampuscommunity.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dewmark.smartcampuscommunity.mapper.PostImageMapper;
import com.dewmark.smartcampuscommunity.mapper.PostTagMapper;
import com.dewmark.smartcampuscommunity.pojo.entity.PostImage;
import com.dewmark.smartcampuscommunity.pojo.entity.PostTag;
import com.dewmark.smartcampuscommunity.service.PostImageService;
import com.dewmark.smartcampuscommunity.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/12/01
 **/
@Service
public class PostImageServiceImpl extends ServiceImpl<PostImageMapper, PostImage> implements PostImageService {

    private final PostImageMapper postImageMapper;
    public PostImageServiceImpl(PostImageMapper postImageMapper) {
        this.postImageMapper = postImageMapper;
    }

    @Override
    public List<PostImage> find(Long postId){
        List<PostImage> postImages = postImageMapper.list(postId);
        return postImages;
    }

}
