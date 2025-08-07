package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * User表取得用户数据设置身份验证用的Userdetails
     *
     * @param userName 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 无用户异常
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // 取得用户数据
        User user = userRepository.findByUsername(userName).orElseThrow(() ->
            new UsernameNotFoundException("这个用户不存在。userName: " + userName));

        // 将取到的用户数据设置到userdetails并返回
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
    }
}
