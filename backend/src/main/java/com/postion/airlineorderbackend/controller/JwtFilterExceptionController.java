package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class JwtFilterExceptionController {
    @RequestMapping("/error/jwtFilter")
    public void jwtFilterException(HttpServletRequest request) throws Exception {
        Exception jwtException = (Exception) request.getAttribute("jwtFilter.error");
        throw jwtException;
    }

}
