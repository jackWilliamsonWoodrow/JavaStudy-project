package com.test.mapper;

import com.test.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM db_account where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

}
