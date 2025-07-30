package com.postion.airlineorderbackend.exception;

/**
 * 编辑响应信息
 * @param <T> 类型参数
 */
public class ResponseMessage<T> {
    private int code;
    private String msg;
    private T data;

    public ResponseMessage(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 返回200成功消息
     * @param data 数据
     * @return 响应消息
     * @param <T> 类型参数
     *
     */
    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(200, "success", data);
    }
    /**
     * 返回201登陆成功消息
     * @param data 数据
     * @return 响应消息
     * @param <T> 类型参数
     *
     */
    public static <T> ResponseMessage<T> created(T data) {
        return new ResponseMessage<>(201, "created", data);
    }

    /**
     * 返回错误消息
     * @param code 错误code
     * @param message 错误信息
     * @return 响应信息
     * @param <T> 类型参数
     */
    public static <T> ResponseMessage<T> error(int code, String message) {
        return new ResponseMessage<>(code, message, null);
    }

    /**
     * 返回错误消息
     * @param code 错误code
     * @param message 错误信息
     * @param data 数据
     * @return 响应信息
     * @param <T> 类型参数
     */
    public static <T> ResponseMessage<T> error(int code, String message, T data) {
        return new ResponseMessage<>(code, message, data);
    }

}
