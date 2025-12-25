package com.dewmark.smartcampuscommunity.controller.user;

import com.dewmark.smartcampuscommunity.pojo.dto.UserUpdateDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersLoginDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersRegisterDTO;
import com.dewmark.smartcampuscommunity.pojo.vo.UserInfoVO;
import com.dewmark.smartcampuscommunity.pojo.vo.UserLoginVO;
import com.dewmark.smartcampuscommunity.result.Result;
import com.dewmark.smartcampuscommunity.service.UsersService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    /**
     * 根据id查询用户信息
     * @param id
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 25/12/2025
     **/
    @GetMapping("/{id}")
    public Result<UserInfoVO> getUserById(@PathVariable Long id) {
        log.info("根据id查询用户信息: {}", id);
        if (id == null) {
            return Result.error("用户id不能为空！");
        }
        UserInfoVO userInfoVO = usersService.findByUserId(id);
        if (userInfoVO == null) {
            return Result.error("用户不存在！");
        }
        return Result.success(userInfoVO);
    }

    /**
     * 修改用户信息
     * @param userUpdateDTO
     * @return com.dewmark.smartcampuscommunity.result.Result
     * @author dewMark
     * @create 25/12/2025
     **/
    @PutMapping
    public Result updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("修改用户信息: {}", userUpdateDTO);
        if (userUpdateDTO == null || userUpdateDTO.getId() == null) {
            return Result.error("数据不合法！");
        }
        usersService.updateUser(userUpdateDTO);
        return Result.success();
    }

}

