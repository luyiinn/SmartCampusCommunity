package com.dewmark.smartcampuscommunity.service;

import com.dewmark.smartcampuscommunity.pojo.dto.UserUpdateDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersLoginDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersRegisterDTO;
import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import com.dewmark.smartcampuscommunity.pojo.vo.UserLoginVO;
import com.dewmark.smartcampuscommunity.pojo.vo.UserInfoVO;

public interface UsersService {
    void save(UsersRegisterDTO usersRegisterDTO);

    UserLoginVO login(UsersLoginDTO usersLoginDTO);

    UserInfoVO findByUserId(Long id);

    Users getUserById(Long id);

    void updateUser(UserUpdateDTO userUpdateDTO);
}
