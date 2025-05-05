package com.tiv.sqlsession;

import lombok.Data;

@Data
@Table(tableName = "user")
public class User {
    private int id;
    private String name;
    private int age;
}
