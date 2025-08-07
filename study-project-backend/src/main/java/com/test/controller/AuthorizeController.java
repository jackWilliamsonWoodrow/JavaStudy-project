package com.test.controller;

import com.test.entity.RestBean;
import com.test.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final String USERNAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z]+$";
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public RestBean<String> validateEmail(@Pattern (regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                          HttpSession session){
        String b = service.sendValidateEmail(email, session.getId());
        if (b == null){
            return RestBean.success("邮件已发送，请注意查收");
        }
        else
            return RestBean.failure(400,"邮件发送失败，请联系管理员");
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min = 2,max = 8) @RequestParam("username") String username,
                                         @Length(min = 6,max = 16)@RequestParam("password") String password,
                                         @Pattern(regexp = EMAIL_REGEX)@RequestParam("email") String email,
                                         @Length(min = 6,max = 6)@RequestParam("code") String code,
                                         HttpSession session){
        String s = service.validateAndRegister(username, password, email, code, session.getId());
        if (s ==null){
            return RestBean.success("注册成功");
        }else {
            return RestBean.failure(400,s);
        }

    }
}
