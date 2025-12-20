package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.context.BaseContext;
import com.dewmark.smartcampuscommunity.exception.DataNotIlegalException;
import com.dewmark.smartcampuscommunity.pojo.dto.PostQueryDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.PostSaveDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.PageVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PostDetailVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PostListVO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 帖子板块
 * @author: dewMark
 * @date: 2025/11/16
 **/
@Slf4j
@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }


    /**
     * 新增帖子
     * @param postSaveDTO
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 16/11/2025
     **/
    @PostMapping("/add")
    public Result savePost(@RequestBody PostSaveDTO postSaveDTO) {
        if (postSaveDTO == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        log.info("发布帖子: {}", postSaveDTO);
        postService.savePost(postSaveDTO);
        return Result.success();
    }

    /**
     * 帖子条件分页查询
     * @param postQueryDTO
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.PageVO<com.dewmark.smartcampuscommunity.pojo.vo.PostListVO>>
     * @author dewMark
     * @create 16/11/2025
     **/
    @GetMapping("/list")
    public Result<PageVO<PostListVO>> getPostList(PostQueryDTO postQueryDTO) {

        if (postQueryDTO == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }

        PageVO<PostListVO> postlist = postService.page(postQueryDTO);

        log.info("测试图片显示{}",Result.success(postlist));
        return Result.success(postlist);
    }


    /**
     * 用户点赞or取消点赞
     * @param postId
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 27/11/2025
     **/
    @PutMapping("/like/{postId}")
    public Result setLike(@PathVariable Long postId){
        if (postId == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        log.info("用户：{}，点赞OR取消点赞帖子：{}", BaseContext.getCurrentId(),postId);

        postService.setLike(postId);

        return Result.success();
    }

    /**
     * 帖子详情
     * @param postId
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.PostDetailVO>
     * @author dewMark
     * @create 1/12/2025
     **/
    @GetMapping("/detail/{postId}")
    public Result<PostDetailVO> showDetail(@PathVariable Long postId){
        log.info("展示帖子{}的详细内容",postId);
        if (postId == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        PostDetailVO postDetailVO = postService.showDetail(postId);
        return Result.success(postDetailVO);
    }

    /**
     * 获取用户已点赞帖子列表
     * @param postQueryDTO
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.PageVO<com.dewmark.smartcampuscommunity.pojo.vo.PostListVO>>
     * @author dewMark
     * @create 19/12/2025
     **/
    @GetMapping("/liked-list")
    public Result<PageVO<PostListVO>> getLikedPostList(PostQueryDTO postQueryDTO) {
        if (postQueryDTO == null) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }
        PageVO<PostListVO> likedPostList = postService.pageLikedPosts(postQueryDTO);
        return Result.success(likedPostList);
    }

}
