package com.tiv.sqlsession;

import com.tiv.sqlsession.mapper.UserMapper;
import com.tiv.sqlsession.model.User;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("使用MySqlSessionFactory查询: ");
        MySqlSessionFactory mySqlSessionFactory = new MySqlSessionFactory();
        UserMapper mapper = mySqlSessionFactory.getMapper(UserMapper.class);
        System.out.println(mapper.selectById(1));
        System.out.println(mapper.selectByName("Tiv"));
        System.out.println(mapper.selectByIdAndName(1, "Tiv"));

        System.out.println("使用jdbc查询: ");
        System.out.println(jdbcSelectById(1));
    }

    private static User jdbcSelectById(int id) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai";
        String dbUser = "root";
        String password = "root";
        String sql = "SELECT id, name, age FROM user WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, password);
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setAge(resultSet.getInt("age"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
