package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2023/1/3 22:03
 * @email 18668485565163.com
 */
@Getter
@Setter
public abstract class Response implements Serializable {

    protected Boolean success = true;

    protected String code = "200";

    protected String msg = "success";

    public Response() {
    }

    public Response(boolean success) {
        this.success = success;
        this.code = "200";
        this.msg = "处理成功";
    }

    public Response(String code, String msg) {
        this.success = false;
        this.code = code;
        this.msg = msg;
    }

    public Response(boolean success, String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }
}
