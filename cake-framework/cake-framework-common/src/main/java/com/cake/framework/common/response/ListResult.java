package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
public class ListResult<T> extends Response {

    private List<T> content = new ArrayList<>(0);

    private Integer totalCount = 0;

    public ListResult() {
    }

    public ListResult(boolean success, String code, String msg) {
        super(success, code, msg);
    }

    public ListResult(boolean success, String code, String msg, List<T> content) {
        super(success, code, msg);
        this.content = content;
        this.totalCount = content != null && !content.isEmpty() ? content.size() : 0;
    }

    public static <T> ListResult<T> succeed() {
        return new ListResult<>(true, "200", "处理成功");
    }

    public static  <T> ListResult<T> succeed(List<T> data) {
        return new ListResult<>(true, "200", "处理成功", data);
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
