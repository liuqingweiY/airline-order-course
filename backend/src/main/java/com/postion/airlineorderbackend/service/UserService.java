package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.model.User;

public interface UserService {
    User findByUserName(String userName);
}
