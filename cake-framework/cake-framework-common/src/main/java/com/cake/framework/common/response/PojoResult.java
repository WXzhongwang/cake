package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2023/1/3 22:05
 * @email 18668485565163.com
 */
@Getter
@Setter
public class PojoResult<T> extends Response {

    private T content;

    public PojoResult(boolean success, String code, String msg) {
        super(success, code, msg);
    }

    public PojoResult(boolean success, String code, String msg, T content) {
        super(success, code, msg);
        this.content = content;
    }

    public static <T> PojoResult<T> succeed() {
        return new PojoResult<>(true, "200", "处理成功");
    }

    public static <T> PojoResult<T> succeed(T data) {
        return new PojoResult<>(true, "200", "处理成功", data);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("content", content)
                .append("success", success)
                .append("code", code)
                .append("msg", msg)
                .toString();
    }
}
