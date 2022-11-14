package com.cake.framework.fast.bizflow.report;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/10 23:30
 * @email 18668485565163.com
 */
@Data
@Accessors(chain = true)
public class ReportSendMessage implements Serializable {

    private String appName;
    private String flow;
    private String bizCode;
    private String version;
    /**
     * bizflow or handler
     */
    private String type;
    private Object data;
    private String updateVersion;

    @Override
    public String toString() {
        return "ReportSendMessage{" +
                "appName='" + appName + '\'' +
                ", flow='" + flow + '\'' +
                ", bizCode='" + bizCode + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", updateVersion='" + updateVersion + '\'' +
                '}';
    }
}
