package com.test.service.impl;

import com.test.entity.Account;
import com.test.mapper.UserMapper;
import com.test.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    @Resource
    UserMapper userMapper;
    @Value("${spring.mail.username}")
    String from;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    MailSender mailSender;


    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null){
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account account = userMapper.findAccountByNameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");


        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    @Override
    public String sendValidateEmail(String email, String sessionId) {
        /*
        1.先生成对应的验证码
        2.把邮箱和对应的验证码直接放到redis里(过期时间3分钟，)
        3.发送验证码到指定邮箱
        4.如果发送失败，将redis里的键值对删除
        5.用户注册时，从redis里去除对应键值对，然后看验证码是否一致

         */

        String key = "email:"+ sessionId + ":" + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key)))
            return "发送频繁请稍后再试";

        if (userMapper.findAccountByNameOrEmail(email) != null)
            return "此邮箱已被其他用户注册";
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("你的验证码");
        message.setText("验证码是"+code+"有效时间3分钟，请妥善保存！");
        message.setFrom("17602383416@163.com");
        message.setTo(email);
        try{
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set(key,String.valueOf(code),1, TimeUnit.MINUTES);
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "邮件发送失败，请检查邮件地址是否有效";
    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        String key = "email:"+ sessionId + ":" + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            String s  = stringRedisTemplate.opsForValue().get(key);
            if (s.equals(code)) {
                password = encoder.encode(password);
                if (userMapper.createAccount(username,password,email) > 0){
                    return null;
                }else {
                    return "内部错误，请联系管理员";
                }
            }else {
                return "验证码错误，请检查后提交";
            }
        }else {
            return "请先获取验证码";
        }
    }
}
