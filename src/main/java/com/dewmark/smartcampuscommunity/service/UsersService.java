package com.dewmark.smartcampuscommunity.service;


import com.dewmark.smartcampuscommunity.pojo.dto.UsersLoginDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersRegisterDTO;
import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import com.dewmark.smartcampuscommunity.pojo.vo.UserLoginVO;

public interface UsersService {
    void save(UsersRegisterDTO usersRegisterDTO);

    UserLoginVO login(UsersLoginDTO usersLoginDTO);

    Users findByUserId(Long id);
}
