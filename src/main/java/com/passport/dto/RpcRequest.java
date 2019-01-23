package com.passport.dto;

<<<<<<< HEAD
/**
 * RPC请求封装
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
public class RpcRequest {
  private String requestId;//请求ID
  private String interfaceName;//接口名称
  private String methodName;//方法名称
  private Class<?>[] parameterTypes;//参数类型
  private Object[] parameters;//参数对象
=======

public class RpcRequest {

  private String requestId;
  private String interfaceName;
  private String methodName;
  private Class<?>[] parameterTypes;
  private Object[] parameters;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getInterfaceName() {
    return interfaceName;
  }

  public void setInterfaceName(String className) {
    this.interfaceName = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Class<?>[] getParameterTypes() {
    return parameterTypes;
  }

  public void setParameterTypes(Class<?>[] parameterTypes) {
    this.parameterTypes = parameterTypes;
  }

  public Object[] getParameters() {
    return parameters;
  }

  public void setParameters(Object[] parameters) {
    this.parameters = parameters;
  }
}
