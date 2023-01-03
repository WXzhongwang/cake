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
public class Response implements Serializable {

    private Boolean success = true;

    private String errorCode = "200";

    private String errorMessage = "success";

    public Response() {
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("success", success)
                .append("errorCode", errorCode)
                .append("errorMessage", errorMessage)
                .toString();
    }
}
