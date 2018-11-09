package com.utils;

import java.io.Serializable;

/**
 * 公共返回对象
 *
 * @author zhsh
 */
public class ResultInfo implements Serializable {

    /**
     * 返回状态码
     */
    private String code;

    /**
     * 返回信息：错误时返回错误藐视，正确时返回业务信息
     */
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultInfo(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultInfo() {
    }

    public static ResultInfo info(String code, String msg) {
        return new ResultInfo(code, msg);
    }
}
