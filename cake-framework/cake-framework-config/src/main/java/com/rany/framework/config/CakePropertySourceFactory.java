package com.rany.framework.config;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 17:22
 * @slogon 找到银弹
 */
public class CakePropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        return new CakePropertySource(name, resource.getResource().getFilename());
    }

}