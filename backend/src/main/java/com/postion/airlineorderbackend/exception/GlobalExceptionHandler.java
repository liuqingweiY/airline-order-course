package com.postion.airlineorderbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author liuqw
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * RuntimeException类异常处理
     *
     * @param ex RuntimeException
     * @return ResponseEntity
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage<String>> handleResourceNotFoundException(
        RuntimeException ex) {

        ResponseMessage<String> response = ResponseMessage.error(400, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Exception类异常处理
     *
     * @param ex Exception
     * @return ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<String>> handleUnexceptedException(Exception ex) {
        ResponseMessage<String> response = ResponseMessage.error(500,
            "An unexcepted error happened: " + ex.getMessage(), null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
