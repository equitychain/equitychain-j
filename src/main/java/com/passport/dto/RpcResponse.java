package com.passport.dto;

/**
 * RPC响应封装
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
public class RpcResponse {
  private String requestId;//请求ID
  private Exception exception;//异常信息
  private Object result;//响应结果

  /**
   * 是否带有异常
   */
  public boolean hasException() {
    return exception != null;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }
}
