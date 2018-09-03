package com.passport.dto;

import com.passport.enums.ResultEnum;


public class ResultDto<T> {

  private int code;
  private String msg;
  private T data;

  public ResultDto() {
  }

  public ResultDto(ResultEnum resultEnum) {
    this.code = resultEnum.getCode();
    this.msg = resultEnum.getMessage();
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
