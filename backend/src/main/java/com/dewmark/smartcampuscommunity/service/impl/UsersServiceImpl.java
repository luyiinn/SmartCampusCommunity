package com.dewmark.smartcampuscommunity.service.impl;

import com.dewmark.smartcampuscommunity.config.properties.JwtProperties;
import com.dewmark.smartcampuscommunity.constent.JwtClaimsConstant;
import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.exception.AccountNotFoundException;
import com.dewmark.smartcampuscommunity.exception.PasswordErrorException;
import com.dewmark.smartcampuscommunity.mapper.UsersMapper;
import com.dewmark.smartcampuscommunity.pojo.dto.UserUpdateDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersLoginDTO;
import com.dewmark.smartcampuscommunity.pojo.dto.UsersRegisterDTO;
import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import com.dewmark.smartcampuscommunity.pojo.vo.UserInfoVO;
import com.dewmark.smartcampuscommunity.pojo.vo.UserLoginVO;
import com.dewmark.smartcampuscommunity.service.UsersService;
import com.dewmark.smartcampuscommunity.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:用户业务接口实现类
 * @author: dewMark
 * @date: 2025/11/03
 **/
@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UsersMapper usersMapper;
    private final JwtProperties jwtProperties;

    @Autowired
    public UsersServiceImpl(
            UsersMapper usersMapper,
            JwtProperties jwtProperties) {
        this.usersMapper = usersMapper;
        this.jwtProperties = jwtProperties;
    }

    /**
     *
     * 注册用户
     * 
     * @param usersRegisterDTO
     * @return void
     * @author dewMark
     * @create 3/11/2025
     **/
    @Override
    public void save(UsersRegisterDTO usersRegisterDTO) {
        Users users = new Users();
        BeanUtils.copyProperties(usersRegisterDTO, users);
        users.setCreatedAt(LocalDateTime.now());
        users.setUpdatedAt(LocalDateTime.now());
        int res = usersMapper.insert(users);
        if (res > 0) {
            log.info("用户{}注册成功", users.getId());
        }

    }

    /**
     * 处理登录逻辑
     *
     * @param usersLoginDTO
     * @return com.dewmark.smartcampuscommunity.pojo.vo.UserLoginVO
     * @author dewMark
     * @create 15/11/2025
     **/
    @Override
    public UserLoginVO login(UsersLoginDTO usersLoginDTO) {
        String username = usersLoginDTO.getUsername();
        String password = usersLoginDTO.getPassword();

        Users users = usersMapper.selectUserByName(username);

        // 用户存在验证
        if (users == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码验证
        if (!password.equals(users.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, users.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        log.info("用户{}登录成功，token{}", users.getUsername(), token);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(users.getId())
                .userName(users.getUsername())
                .token(token)
                .avatar(users.getAvatar())
                .build();

        return userLoginVO;
    }

    @Override
    public UserInfoVO findByUserId(Long id) {
        Users users = usersMapper.selectById(id);
        if (users == null) {
            return null;
        }
        // 将Users转换为UserInfoVO，不包含密码信息
        UserInfoVO userInfoVO = UserInfoVO.builder()
                .id(users.getId())
                .username(users.getUsername())
                .avatar(users.getAvatar())
                .email(users.getEmail())
                .phone(users.getPhone())
                .studentId(users.getStudentId())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
        return userInfoVO;
    }

    @Override
    public Users getUserById(Long id) {
        // 直接返回Users对象，包含所有信息（包括密码）
        return usersMapper.selectById(id);
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        Users users = new Users();
        // 复制UserUpdateDTO的属性到Users对象
        users.setId(userUpdateDTO.getId());
        users.setUsername(userUpdateDTO.getUsername());
        users.setAvatar(userUpdateDTO.getAvatar());
        users.setEmail(userUpdateDTO.getEmail());
        users.setPhone(userUpdateDTO.getPhone());
        users.setStudentId(userUpdateDTO.getStudentId());
        users.setUpdatedAt(java.time.LocalDateTime.now());
        // 使用MybatisPlus的updateById方法更新用户信息，不会修改密码字段
        usersMapper.updateById(users);
    }
}
