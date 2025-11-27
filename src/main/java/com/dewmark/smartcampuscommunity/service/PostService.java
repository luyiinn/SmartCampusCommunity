package com.dewmark.smartcampuscommunity.service;

import com.dewmark.smartcampuscommunity.pojo.dto.PostQueryDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.PostSaveDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.PageVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PostListVO;

public interface PostService{
    void savePost(PostSaveDTO postSaveDTO);

    PageVO<PostListVO> page(PostQueryDTO postQueryDTO);

    void commentCountUp(Long id);
}
