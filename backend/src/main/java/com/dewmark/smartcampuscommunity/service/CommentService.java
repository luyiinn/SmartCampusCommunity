package com.dewmark.smartcampuscommunity.service;

import com.dewmark.smartcampuscommunity.pojo.dto.CommentDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.CommentVO;

import java.util.List;

public interface CommentService {

    void add(CommentDTO commentDTO);

    List<CommentVO> list(Long postId);
}
