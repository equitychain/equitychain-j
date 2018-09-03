package com.passport.enums;


public enum ResultEnum {

  SUCCESS(200, "SUCCESS"),
  SYS_ERROR(500, "SYS_ERROR"),
  NO_PRIVILEGES(-1, "NO_PRIVILEGES"),
  SIGN_WRONG(-2, "SIGN_WRONG"),
  PARAMS_ERROR(-3, "PARAMS_ERROR"),
  PARAMS_LOSTOREMPTY(-4, "PARAMS_LOSTOREMPTY"),
  HANDLE_WRONG(-5, "HANDLE_WRONG"),

  ACCOUNT_NOT_EXISTS(1001, "ACCOUNT_NOT_EXISTS"),
  ACCOUNT_IS_LOCKED(1002, "ACCOUNT_IS_LOCKED"),
  PASSWORD_WRONG(1003, "PASSWORD_WRONG"),
  ADDRESS_ILLEGAL(1004, "ADDRESS_ILLEGAL"),
  BALANCE_NOTENOUGH(5, "BALANCE_NOTENOUGH");


  private int code;
  private String message;
  private String originalInfo;

  private ResultEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public static ResultEnum statusOf(int index) {
    for (ResultEnum resultEnum : ResultEnum.values()) {
      if (resultEnum.getCode() == index) {
        return resultEnum;
      }
    }
    return null;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getOriginalInfo() {
    return originalInfo;
  }

  public void setOriginalInfo(String originalInfo) {
    this.originalInfo = originalInfo;
  }
}
