package com.wx.cloudnotes.dao;

import com.wx.cloudnotes.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserMapper {
    List<User> selectAll();

    User selectOne(Integer id);

    void save(User user);
}
