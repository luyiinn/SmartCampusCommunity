package com.dewmark.smartcampuscommunity.service.impl;

import com.dewmark.smartcampuscommunity.context.BaseContext;
import com.dewmark.smartcampuscommunity.mapper.CommentMapper;
import com.dewmark.smartcampuscommunity.mapper.PostMapper;
import com.dewmark.smartcampuscommunity.mapper.UsersMapper;
import com.dewmark.smartcampuscommunity.pojo.dto.CommentDTO;
import com.dewmark.smartcampuscommunity.pojo.entity.Comment;
import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import com.dewmark.smartcampuscommunity.pojo.vo.CommentVO;
import com.dewmark.smartcampuscommunity.service.CommentService;
import com.dewmark.smartcampuscommunity.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/24
 **/
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UsersMapper usersMapper;
    private final PostService postService;
    @Autowired
    public CommentServiceImpl(
            CommentMapper commentMapper,
            UsersMapper usersMapper,
            PostService postService) {
        this.commentMapper = commentMapper;
        this.usersMapper = usersMapper;
        this.postService = postService;
    }

    /**
     * 新增评论
     * @param commentDTO
     * @return void
     * @author dewMark
     * @create 24/11/2025
     **/
    @Override
    public void add(CommentDTO commentDTO) {
        Comment comment = Comment.builder()
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .userId(BaseContext.getCurrentId())
                .isDeleted(0)
                .likeCount(0)
                .build();
        BeanUtils.copyProperties(commentDTO, comment);

        // 修改post中评论数+1
        postService.commentCountUp(commentDTO.getPostId());

        commentMapper.insert(comment);
    }

    /**
     * 根据帖子id展示评论列表
     * @param postId
     * @return com.dewmark.smartcampuscommunity.pojo.vo.CommentListVo
     * @author dewMark
     * @create 24/11/2025
     **/
    @Override
    public List<CommentVO> list(Long postId) {
         List<Comment> comments = commentMapper.list(postId);
         List<CommentVO> commentVOList = new ArrayList<>();

         // 为CommentVO补充用户名，用户头像
         comments.forEach(comment -> {
            Users users = usersMapper.selectById(comment.getUserId());
            CommentVO commentVO
                    = new CommentVO()
                    .builder()
                    .avatar(users.getAvatar())
                    .userName(users.getUsername())
                    .build();
            BeanUtils.copyProperties(comment, commentVO);
            commentVOList.add(commentVO);
        });

        return commentVOList;
    }
}
