package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.pojo.vo.TagListVO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.PostTagService;
import com.dewmark.smartcampuscommunity.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/16
 **/
@RestController
@RequestMapping("/api/tag")
public class PostTagController {

    private final PostTagService postTagService;
    private final TagService tagService;
    @Autowired
    public PostTagController(
            PostTagService postTagService,
            TagService tagService ) {
        this.postTagService = postTagService;
        this.tagService = tagService;
    }

    /**
     *  展示标签
     * @return com.dewmark.smartcampuscommunity.result.Result<com.dewmark.smartcampuscommunity.pojo.vo.TagListVO>
     * @author dewMark
     * @create 16/11/2025
     **/
    @GetMapping("/list")
    public Result<TagListVO> getTagList(){
        TagListVO tagListVO = new TagListVO();
        tagListVO.setTags(tagService.selectList());
        return Result.success(tagListVO);
    }
}
