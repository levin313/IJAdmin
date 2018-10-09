package com.levin.common.base;

/**
 * Json对象用于向前端传递数据
 *
 * @author Levin
 */

public class Json implements java.io.Serializable {

    private boolean success = false;

    private String msg = "";

    private Object data = null;

    private Integer code = 200;

    public boolean isSuccess() {
        return success;
    }

    public Json setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Json setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Json setData(Object data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public Json setCode(Integer code) {
        this.code = code;
        return this;
    }
}

