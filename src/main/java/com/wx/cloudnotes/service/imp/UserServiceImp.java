package com.wx.cloudnotes.service.imp;

import com.wx.cloudnotes.dao.UserMapper;
import com.wx.cloudnotes.domain.User;
import com.wx.cloudnotes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();

    }

    @Override
    public User selectOne(Integer id) {
        return userMapper.selectOne(id);
    }

    @Override
    public void save(User user) {

    }
}
