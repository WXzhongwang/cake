package com.cake.framework.mybatis.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * MappedJdbcTypes 这个注解定义的是JdbcType类型，这里的类型不可自己随意定义，必须要是枚举类org.apache.ibatis.type.JdbcType所枚举的数据类型
 * MappedTypes 这里定义的是JavaType的数据类型，描述了哪些Java类型可被拦截
 * */
@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({List.class})
public class ListStringTypeHandler implements TypeHandler<List<String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        String hobbys = StringUtils.join(parameter, ",");
        try {
            ps.setString(i, hobbys);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        return Arrays.asList(rs.getString(columnName).split(","));
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return Arrays.asList(rs.getString(columnIndex).split(","));
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String hobbys = cs.getString(columnIndex);
        return Arrays.asList(hobbys.split(","));
    }
}
