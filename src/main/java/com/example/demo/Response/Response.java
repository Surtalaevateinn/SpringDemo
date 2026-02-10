package com.example.demo.Response;

import lombok.Data;

@Data
public class Response <T>{
    private T data;
    private boolean success;
    private String errorMsg;

    public static <K> Response newSuccess(K data){
        Response<K> response = new Response<>();
        response.setData(data);
        response.setSuccess(true);
        return response;

    }

    public static Response<Void> newFail(String errorMsg){
        Response<Void> response = new Response<>();
        response.setErrorMsg(errorMsg);
        response.setSuccess(false);
        return response;

    }
}
