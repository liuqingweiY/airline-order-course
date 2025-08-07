package com.postion.airlineorderbackend.exception;

import com.postion.airlineorderbackend.contents.BusinessContents;
import lombok.Data;

@Data
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static<T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(BusinessContents.SUCCESS);
        response.setData(data);
        return response;
    }

    public static<T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
