package com.dewmark.smartcampuscommunity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dewmark.smartcampuscommunity.pojo.entity.PostImage;

import java.util.List;

public interface PostImageService extends IService<PostImage> {

    public List<PostImage> find(Long postId);

}
