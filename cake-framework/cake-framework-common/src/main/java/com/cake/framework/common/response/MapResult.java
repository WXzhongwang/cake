package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;

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
}
