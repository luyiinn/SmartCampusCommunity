package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.context.BaseContext;
import com.dewmark.smartcampuscommunity.exception.DataNotIlegalException;
import com.dewmark.smartcampuscommunity.pojo.dto.CommentDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.CommentVO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 处理帖子评论 Controller
 * @author: dewMark
 * @date: 2025/11/24
 **/
@Slf4j
@RequestMapping("/api/comment")
@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 新增，发表评论
     * 
     * @param commentDTO
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 24/11/2025
     **/
    @PostMapping("/add")
    public Result add(@RequestBody CommentDTO commentDTO) {
        log.info("新增评论{}", commentDTO);
        if (commentDTO == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        commentService.add(commentDTO);
        return Result.success();
    }

    /**
     * 根据帖子id查询评论列表
     * 
     * @param postId
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.CommentListVo>
     * @author dewMark
     * @create 24/11/2025
     **/
    @GetMapping("/list")
    public Result<List<CommentVO>> list(Long postId) {
        log.info("展示帖子id：{}的评论列表", postId);

        if (postId == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        List<CommentVO> list = commentService.list(postId);

        return Result.success(list);
    }

    /**
     * 用户点赞or取消点赞评论
     * 
     * @param commentId
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 19/12/2025
     **/
    @PutMapping("/like/{commentId}")
    public Result setLike(@PathVariable Long commentId) {
        if (commentId == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        log.info("用户：{}，点赞OR取消点赞评论：{}", BaseContext.getCurrentId(), commentId);

        commentService.setLike(commentId);

        return Result.success();
    }
}
