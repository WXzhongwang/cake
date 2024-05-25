package com.cake.framework.mybatis.handler;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({Object.class})
public class GenericObjectTypeHandler<T> implements TypeHandler<T> {

    private final Class<T> type;

    public GenericObjectTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, JdbcType.VARCHAR.TYPE_CODE);
        } else {
            ps.setString(i, JSON.toJSONString(parameter));
        }
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = rs.getString(columnName);
        return jsonString == null ? null : JSON.parseObject(jsonString, type);
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = rs.getString(columnIndex);
        return jsonString == null ? null : JSON.parseObject(jsonString, type);
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = cs.getString(columnIndex);
        return jsonString == null ? null : JSON.parseObject(jsonString, type);
    }
}
