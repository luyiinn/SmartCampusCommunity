package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.pojo.dto.UsersLoginDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersRegisterDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.UserLoginVO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.UsersService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
/**
 * @description:
 * @author: dewMark
 * @date: 2025/11/03
 **/
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    final UsersService usersService;
    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * 用户注册
     * @param usersRegisterDTO
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 3/11/2025
     **/
    @PostMapping("/regis")
    public Result register(@RequestBody UsersRegisterDTO usersRegisterDTO) {
        log.info("用户注册{}",usersRegisterDTO);
        usersService.save(usersRegisterDTO);
        return Result.success();
    }

    /**
     * 用户登录
     * @param usersLoginDTO
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 15/11/2025
     **/
    @PostMapping("/login")
    public Result login(@RequestBody UsersLoginDTO usersLoginDTO){
        log.info("用户登录{}",usersLoginDTO);
        if(usersLoginDTO == null || usersLoginDTO.getUsername() == null || usersLoginDTO.getPassword() == null){
            return Result.error("数据不合法！");
        }
        UserLoginVO userLoginVO = usersService.login(usersLoginDTO);
        return Result.success(userLoginVO);
    }


}

