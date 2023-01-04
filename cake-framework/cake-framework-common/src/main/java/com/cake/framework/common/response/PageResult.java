package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
public class PageResult<T> extends Response {

    private Page<T> content;


    public PageResult() {
    }

    public PageResult(boolean success, String code, String msg) {
        super(success, code, msg);
    }

    public PageResult(boolean success, String code, String msg, Page<T> content) {
        super(success, code, msg);
        this.content = content;
    }

    public static <T> PageResult<T> succeed() {
        return new PageResult<>(true, "200", "处理成功");
    }

    public static <T> PageResult<T> succeed(Page<T> data) {
        return new PageResult<>(true, "200", "处理成功", data);
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
