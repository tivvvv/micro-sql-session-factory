package com.tiv.sqlsession;

import com.tiv.sqlsession.annotation.Param;

public interface UserMapper {

    User selectById(@Param("id") int id);
}
