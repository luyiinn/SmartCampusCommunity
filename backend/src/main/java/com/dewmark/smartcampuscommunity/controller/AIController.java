package com.dewmark.smartcampuscommunity.controller;

import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.AIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:处理AI相关请求
 * @author: dewMark
 * @date: 2025/12/25
 **/
@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Resource
    private AIService aiService;

    @PostMapping("/copywrite")
    public Result<String> copywrite(@RequestBody String originalText){
        log.info("优化文案：{}",originalText);

        String res = aiService.copywrite(originalText);

        return Result.success(res);
    }

}
