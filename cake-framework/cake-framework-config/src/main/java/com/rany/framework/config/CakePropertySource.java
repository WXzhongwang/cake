package com.rany.framework.config;

import org.springframework.core.env.PropertySource;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 17:21
 * @slogon 找到银弹
 */
public class CakePropertySource extends PropertySource<String> {

    CakePropertySource(String name, String value) {
        super(name);
        Config.init(value);
    }

    @Override
    public String getProperty(String placeholder) {
        try {
            String result = Config.getStaticMapData().getProperty(placeholder);
            if (result == null) {
                return Config.getData(placeholder);
            } else {
                return result;
            }
        } catch (Exception e) {

        }
        return null;
    }

}
