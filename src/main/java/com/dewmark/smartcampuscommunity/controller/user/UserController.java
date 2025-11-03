package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.pojo.dto.UsersDTO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.UsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/03
 **/
@Api(tags = "用户相关接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    final UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * 用户注册
     * @param usersDTO
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 3/11/2025
     **/
    @ApiOperation("用户注册")
    @PostMapping("/regis")
    public Result register(@RequestBody UsersDTO usersDTO) {
        log.info("用户注册{}",usersDTO);
        usersService.save(usersDTO);
        return Result.success();
    }


}

