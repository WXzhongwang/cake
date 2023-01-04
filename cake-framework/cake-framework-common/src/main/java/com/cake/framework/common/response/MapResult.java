package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class MapResult<K, V> extends Response {

    private Map<K, V> content = new HashMap<>(0);

    public MapResult() {
    }

    public MapResult<K, V> add(K key, V val) {
        if (this.content == null) {
            this.setContent(new HashMap<>(0));
        }
        this.getContent().put(key, val);
        return this;
    }


    public MapResult(boolean success, String code, String msg) {
        super(success, code, msg);
    }

    public MapResult(boolean success, String code, String msg, Map<K, V> content) {
        super(success, code, msg);
        this.content = content;
    }

    public static <K, V> MapResult<K, V> succeed() {
        return new MapResult<>(true, "200", "处理成功");
    }

    public static <K, V> MapResult<K, V> succeed(Map<K, V> data) {
        return new MapResult<>(true, "200", "处理成功", data);
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
