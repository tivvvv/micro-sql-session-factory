package com.tiv.sqlsession.mapper;

import com.tiv.sqlsession.annotation.Param;
import com.tiv.sqlsession.model.User;

public interface UserMapper {

    User selectById(@Param("id") int id);

    User selectByName(@Param("name") String name);

    User selectByIdAndName(@Param("id") int id, @Param("name") String name);
}
