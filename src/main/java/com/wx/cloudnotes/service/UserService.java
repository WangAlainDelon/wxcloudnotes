package com.wx.cloudnotes.service;

import com.wx.cloudnotes.domain.User;

import java.util.List;

public interface UserService {
    List<User> selectAll();

    User selectOne(Integer id);

    void save(User user);
}
