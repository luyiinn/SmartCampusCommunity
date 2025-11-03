package com.dewmark.smartcampuscommunity.mapper;

import com.dewmark.smartcampuscommunity.pojo.entity.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper {

    int save(Users users);
}
