package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUserName(String userName) {

        if (userName.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名必须输入。");
        }
        // 返回查询到的用户数据
        User user =  userRepository.findByUsername(userName).orElseThrow(() ->
            new BusinessException(HttpStatus.BAD_REQUEST, "这个用户不存在"));
        return user;
    }

}
