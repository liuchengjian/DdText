package com.lchj.ddtexts.mvp.model.entity;

import com.lchj.ddtexts.mvp.model.api.Api;

import java.io.Serializable;

/**
 * @Description: 如果你服务器返回的数据固定为这种方式(字段名可根据服务器更改)
 * 替换范型即可重用BaseJson
 */
public class BaseJson<T> implements Serializable {
    private T data;
    private String msg;
    private Integer code;

    /**
     * 请求是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        if (code==Api.STATE_OK) {
            return true;
        } else {
            return false;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
