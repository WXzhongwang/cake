package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
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
public class CollectionResult<T> extends Response {

    private Collection<T> content = new ArrayList<>(0);

    private Integer totalCount;

    public CollectionResult() {
    }

    public CollectionResult(boolean success, String code, String msg) {
        super(success, code, msg);
    }

    public CollectionResult(boolean success, String code, String msg, List<T> content) {
        super(success, code, msg);
        this.content = content;
        this.totalCount = content != null && !content.isEmpty() ? content.size() : 0;
    }

    public static <T> CollectionResult<T> succeed() {
        return new CollectionResult<>(true, "200", "处理成功");
    }

    public static  <T> CollectionResult<T> succeed(List<T> data) {
        return new CollectionResult<>(true, "200", "处理成功", data);
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
