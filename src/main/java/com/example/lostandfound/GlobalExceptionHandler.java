package com.example.lostandfound;

import ch.qos.logback.core.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFileUploadException(MaxUploadSizeExceededException exc, Model model) {
        //model.addAttribute("error", "File is too large. Maximum upload size is 5MB.");
        return "error";
    }
}
