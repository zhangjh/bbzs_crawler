package me.zhangjh.crawler.controller.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Data
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 5077245064332021087L;

    private boolean success = true;
    private T data;
    private String errorMsg;

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setData(data);
        return response;
    }

    public static <T> Response<T> fail(String errorMsg) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setErrorMsg(errorMsg);
        return response;
    }
}
