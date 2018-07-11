package com.passport.dto;

/**
 * 客户端返回包装
 * @author 作者xujianfeng 
 * @date 创建时间：2016年11月3日 上午11:28:02
 */
public class ResultDto<T> {
    private int code;
    private String msg;
    private T data;//包装成功返回的数据

    public ResultDto() {
    }

    public ResultDto(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultDto(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
