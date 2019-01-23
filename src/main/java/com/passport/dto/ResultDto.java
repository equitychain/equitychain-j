package com.passport.dto;

import com.passport.enums.ResultEnum;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 客户端返回包装
 * @author 作者xujianfeng 
 * @date 创建时间：2016年11月3日 上午11:28:02
 */
public @ResponseBody class ResultDto<T> {
    private int code;
    private String msg;
    private T data;//包装成功返回的数据

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
=======


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
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
