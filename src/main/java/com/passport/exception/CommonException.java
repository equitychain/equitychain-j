package com.passport.exception;

import com.passport.enums.ResultEnum;


public class CommonException extends RuntimeException {

  private static final long serialVersionUID = -65570670763707623L;

  private ResultEnum resultEnum;


  public CommonException(ResultEnum resultEnum) {
    this.resultEnum = resultEnum;
  }


  public CommonException(ResultEnum resultEnum, String originalInfo) {
    this.resultEnum = resultEnum;
    this.resultEnum.setOriginalInfo(originalInfo);
  }

  public ResultEnum getResultEnum() {
    return resultEnum;
  }
}
