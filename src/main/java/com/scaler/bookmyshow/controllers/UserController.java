package com.scaler.bookmyshow.controllers;

import com.scaler.bookmyshow.models.User;
import com.scaler.bookmyshow.dtos.ResponseStatus;
import com.scaler.bookmyshow.dtos.SignUpRequestDto;
import com.scaler.bookmyshow.dtos.SignUpResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.scaler.bookmyshow.services.UserService;

@Controller
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public SignUpResponseDto signUp(SignUpRequestDto request) {
        SignUpResponseDto response = new SignUpResponseDto();
        User user;

        try {
            user = userService.signUp(request.getEmail(), request.getPassword());
            response.setResponseStatus(ResponseStatus.SUCCESS);
            response.setUserId(user.getId());
        } catch(Exception ex) {
            response.setResponseStatus(ResponseStatus.FAILURE);
        }

        return response;
    }
}
