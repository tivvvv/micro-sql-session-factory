package com.tiv.sqlsession;

import com.tiv.sqlsession.annotation.Param;
import com.tiv.sqlsession.annotation.Table;

import java.lang.reflect.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MySqlSessionFactory {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String PASSWORD = "root";

    @SuppressWarnings("all")
    public <T> T getMapper(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new MapperInvocationHandler());
    }

    static class MapperInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().startsWith("select")) {
                return invokeSelect(proxy, method, args);
            }
            return null;
        }

        private Object invokeSelect(Object proxy, Method method, Object[] args) {
            String sql = buildSelectSql(method);
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, PASSWORD);
                 PreparedStatement statement = conn.prepareStatement(sql)) {

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

        /**
         * 构造查询sql
         *
         * @param method
         * @return
         */
        private String buildSelectSql(Method method) {
            StringBuilder sqlBuilder = new StringBuilder();

            sqlBuilder.append("SELECT ");
            List<String> selectColumns = getSelectColumns(method.getReturnType());
            sqlBuilder.append(String.join(",", selectColumns));
            sqlBuilder.append(" FROM ");

            String tableName = getSelectTableName(method.getReturnType());
            sqlBuilder.append(tableName);

            String whereSql = getSelectWhere(method);
            sqlBuilder.append(whereSql);

            return sqlBuilder.toString();
        }

        /**
         * 获取查询的列
         *
         * @param returnType
         * @return
         */
        private List<String> getSelectColumns(Class<?> returnType) {
            Field[] declaredFields = returnType.getDeclaredFields();
            return Arrays.stream(declaredFields).map(Field::getName).collect(Collectors.toList());
        }

        /**
         * 获取查询的表
         *
         * @param returnType
         * @return
         */
        private String getSelectTableName(Class<?> returnType) {
            Table table = returnType.getAnnotation(Table.class);
            if (table == null) {
                throw new RuntimeException("无法确定查询的表");
            }
            return table.tableName();
        }

        /**
         * 获取查询的where条件
         *
         * @param method
         * @return
         */
        private String getSelectWhere(Method method) {
            Parameter[] parameters = method.getParameters();
            return Arrays.stream(parameters).map((parameter) -> {
                Param param = parameter.getAnnotation(Param.class);
                String column = param.value();
                return column + " = ?";
            }).collect(Collectors.joining(" and "));
        }
    }

}

