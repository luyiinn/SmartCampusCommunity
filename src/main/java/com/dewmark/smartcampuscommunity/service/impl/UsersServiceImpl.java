package com.dewmark.smartcampuscommunity.service.impl;

import com.dewmark.smartcampuscommunity.mapper.UsersMapper;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersDTO;
import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import com.dewmark.smartcampuscommunity.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @description:用户业务接口实现类
 * @author: dewMark
 * @date: 2025/11/03
 **/
@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UsersMapper usersMapper;

    @Autowired
    public UsersServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    /**
     *
     * 注册用户
     * @param usersDTO
     * @return void
     * @author dewMark
     * @create 3/11/2025
     **/

    @Override
    public void save(UsersDTO usersDTO) {
        Users users = new Users();
        BeanUtils.copyProperties(usersDTO,users);
        users.setCreatedAt(LocalDateTime.now());
        users.setUpdatedAt(LocalDateTime.now());
        int res = usersMapper.save(users);
        if(res > 0){
            log.info("用户{}注册成功",users.getId());
        }

    }
}
