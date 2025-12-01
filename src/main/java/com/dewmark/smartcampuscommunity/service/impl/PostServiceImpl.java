package com.dewmark.smartcampuscommunity.service.impl;

import com.dewmark.smartcampuscommunity.config.properties.JwtProperties;
import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.context.BaseContext;
import com.dewmark.smartcampuscommunity.exception.BaseException;
import com.dewmark.smartcampuscommunity.mapper.PostMapper;
import com.dewmark.smartcampuscommunity.mapper.PostTagMapper;
import com.dewmark.smartcampuscommunity.mapper.TagMapper;
import com.dewmark.smartcampuscommunity.mapper.UserPostLikeMapper;
import com.dewmark.smartcampuscommunity.pojo.bo.PostQueryBO;
import com.dewmark.smartcampuscommunity.pojo.dto.PostQueryDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.PostSaveDTO;
import com.dewmark.smartcampuscommunity.pojo.entity.*;
import com.dewmark.smartcampuscommunity.pojo.vo.PageVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PostDetailVO;
import com.dewmark.smartcampuscommunity.pojo.vo.PostListVO;
import com.dewmark.smartcampuscommunity.service.*;
import com.dewmark.smartcampuscommunity.utils.JwtUtil;
import com.fasterxml.jackson.databind.JsonSerializable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: PostService实现类
 * @author: dewMark
 * @date: 2025/11/16
 **/
@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostTagMapper postTagMapper;
    private final PostTagService postTagService;
    private final TagMapper tagMapper;
    private final UsersService usersService;
    private final UserPostLikeMapper userPostLikeMapper;
    private final PostImageService postImageService;
    @Autowired
    public PostServiceImpl(
            PostMapper postMapper,
            PostTagMapper postTagMapper,
            PostTagService postTagService,
            TagMapper tagMapper,
            UsersService usersService,
            UserPostLikeMapper userPostLikeMapper,
            PostImageService postImageService) {
        this.postMapper = postMapper;
        this.postTagMapper = postTagMapper;
        this.postTagService = postTagService;
        this.tagMapper = tagMapper;
        this.usersService = usersService;
        this.userPostLikeMapper = userPostLikeMapper;
        this.postImageService = postImageService;
    }

    /**
     * 发布新帖子业务层逻辑
     * @param postSaveDTO
     * @return void
     * @author dewMark
     * @create 16/11/2025
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)// 明确指定回滚的异常类型
    public void savePost(PostSaveDTO postSaveDTO) {
        // 新增帖子
        Post post = new Post()
                .builder()
                .title(postSaveDTO.getTitle())
                .content(postSaveDTO.getContent())
                .userId(BaseContext.getCurrentId())
                .status(postSaveDTO.getStatus())
                .isAnonymous(postSaveDTO.getIsAnonymous())
                .isDeleted(0)
                .likeCount(0)
                .viewCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        int insert = postMapper.insert(post);
        if (insert != 1) {
            throw new BaseException(MessageConstant.DATABASE_OPRATE);
        }
        Long postId = post.getId();
        // 新增帖子与标签关系
        List<Long> tags = postSaveDTO.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<PostTag> postTags = new ArrayList<>();

            for (Long tagId : tags) {
                PostTag postTag = new PostTag();
                postTag.setTagId(tagId);
                postTag.setPostId(postId);  // 使用回填的主键
                postTags.add(postTag);
            }

            // 批量插入标签关系
            boolean b = postTagService.saveBatch(postTags);
            if (!b) {
                throw new BaseException(MessageConstant.DATABASE_OPRATE);
            }
        }

        // 标签使用次数加 1
        postSaveDTO.getTags().forEach(tagId->{
            tagMapper.useCountUp(tagId);
        });

        List<String> images = postSaveDTO.getImages();
        if (images != null && !images.isEmpty()) {
            List<PostImage> pis = images.stream().map(imagePath -> {
                PostImage postImage = new PostImage();
                postImage.setPostId(postId);
                postImage.setImagePath(imagePath);
                postImage.setCreatedAt(LocalDateTime.now());
                return postImage;
            }).collect(Collectors.toList());

            postImageService.saveBatch(pis);
        }

    }

    /**
     *  帖子分页查询
     * @param postQueryDTO
     * @return com.dewmark.smartcampuscommunity.pojo.vo.PageVO<com.dewmark.smartcampuscommunity.pojo.vo.PostListVO>
     * @author dewMark
     * @create 16/11/2025
     **/
    @Override
    public PageVO<PostListVO> page(PostQueryDTO postQueryDTO) {
        // 构建查询BO
        PostQueryBO queryBO = PostQueryBO.builder()
                .keyword(postQueryDTO.getKeyword())
                .tagId(postQueryDTO.getTagId())
                .offset((postQueryDTO.getPage() - 1) * postQueryDTO.getSize())
                .limit(postQueryDTO.getSize())
                .userId(postQueryDTO.getUserId())
                .status(postQueryDTO.getStatus())
                .build();

        // 查询总数量
        Long total = postMapper.selectPostCount(queryBO);

        // 查询数据列表（SQL中处理内容摘要）
        List<PostListVO> postListVOS = postMapper.selectPostListWithSummary(queryBO);

        // 为每个帖子查询标签
        for (PostListVO vo : postListVOS) {
            vo.setTags(tagMapper.selectTagNamesByPostId(vo.getId()));
        }



        // 为每个帖子查询用户头像和名称，以及是否已点赞
        postListVOS.forEach(postListVO -> {
            Users user = usersService.findByUserId(postListVO.getUserId());
            postListVO.setUserName(user.getUsername());
            postListVO.setAvatar(user.getAvatar());
            Integer isLike = 0;
            List<UserPostLike> exist = userPostLikeMapper.isExist(BaseContext.getCurrentId(), postListVO.getId());
            if (exist != null && !exist.isEmpty()) {
                if (exist.get(0).getLikeStatus() == 1){
                    isLike = 1;
                }
            }
            postListVO.setIsLike(isLike);
        });

        // 为每个帖子查询图片
        postListVOS.forEach(postListVO -> {
            postListVO.setImages(postImageService.find(postListVO.getId()).stream().map(postImage -> {
                return postImage.getImagePath();
            }).collect(Collectors.toList()));
        });

        return new PageVO<>(total, postListVOS);

    }


    /**
     * 评论数+1
     * @return void
     * @author dewMark
     * @create 24/11/2025
     **/
    @Override
    public void commentCountUp(Long id) {
        Integer i = postMapper.commentCountUp(id);
        if (i != 1){
            throw new BaseException(MessageConstant.DATABASE_OPRATE);
        }
    }

    /**
     * 点赞OR取消点赞
     * @return void
     * @author dewMark
     * @create 27/11/2025
     **/
    @Override
    @Transactional
    public void setLike(Long postId) {
        Long userId = BaseContext.getCurrentId();
        Post post = postMapper.selectById(postId);

        // 查询用户的点赞记录
        List<UserPostLike> userPostLikes = userPostLikeMapper.isExist(userId, postId);

        // 判断是否存在记录
        boolean isExist = userPostLikes != null && !userPostLikes.isEmpty();

        if (isExist) {
            // 存在记录，获取最新记录的状态
            UserPostLike latestRecord = userPostLikes.get(0);
            Integer currentStatus = latestRecord.getLikeStatus();

            if (currentStatus == 1) {
                // 当前是点赞状态，执行取消点赞
                UserPostLike updateEntity = UserPostLike.builder()
                        .id(latestRecord.getId())
                        .likeStatus(0)
                        .updatedAt(LocalDateTime.now())
                        .build();
                userPostLikeMapper.updateById(updateEntity);

                // 帖子点赞数+1
                postMapper.updateById(new Post()
                                .builder()
                                .id(postId)
                                .likeCount(post.getLikeCount() - 1)
                                .build());

            } else {
                // 当前是取消点赞状态，执行重新点赞
                UserPostLike updateEntity = UserPostLike.builder()
                        .id(latestRecord.getId())
                        .likeStatus(1)
                        .updatedAt(LocalDateTime.now())
                        .build();
                userPostLikeMapper.updateById(updateEntity);
                // 帖子点赞数-1
                postMapper.updateById(new Post()
                        .builder()
                        .id(postId)
                        .likeCount(post.getLikeCount() + 1)
                        .build());
            }
        } else {
            // 不存在记录，创建新的点赞记录
            UserPostLike newEntity = UserPostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .likeStatus(1)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userPostLikeMapper.insert(newEntity);
            // 帖子点赞数+1
            postMapper.updateById(new Post()
                    .builder()
                    .id(postId)
                    .likeCount(post.getLikeCount() + 1)
                    .build());
        }
    }

    @Override
    public PostDetailVO showDetail(Long postId) {
        PostDetailVO postDetailVO = new PostDetailVO();
        Post post = postMapper.selectById(postId);
        BeanUtils.copyProperties(post,postDetailVO);
        return null;
    }
}
