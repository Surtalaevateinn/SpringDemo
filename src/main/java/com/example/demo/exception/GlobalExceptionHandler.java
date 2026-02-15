package com.example.demo.exception;

import com.example.demo.Response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response<Void> handleIllegalState(IllegalStateException e) {
        System.err.println("🔥 Catched " + e.getMessage());
        return Response.newFail(e.getMessage());
    }

}