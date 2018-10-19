package com.passport.enums;

/**
 * 状态返回，多服务共用
 *
 * @author 作者xujianfeng
 * @date 创建时间：2016年11月4日 上午10:49:17
 */
public enum ResultEnum {
    //***通用状态***
    SUCCESS(200, "操作成功"),
    SYS_ERROR(500, "系统异常"),
    NO_PRIVILEGES(-1, "无请求权限"),
    SIGN_WRONG(-2, "签名加密串不合法"),
    PARAMS_ERROR(-3, "请求参数有误"),
    PARAMS_LOSTOREMPTY(-4, "缺少请求参数或请求参数值为空"),
    HANDLE_WRONG(-5, "处理失败"),
    //***通用状态***

    //***账户***
    ACCOUNT_NOT_EXISTS(1001, "账户不存在"),
    ACCOUNT_IS_LOCKED(1002, "账户未解锁"),
    PASSWORD_WRONG(1003, "交易密码错误"),
    ADDRESS_ILLEGAL(1004, "钱包地址不合法"),
    BALANCE_NOTENOUGH(1005, "余额不足"),
    TRADETYPE_NOTFOUND(1006, "交易类型不存在"),
    TRADEAMOUNT_ILLEGAL(1007, "交易金额不合法"),
    TRUSTEE_EXISTS(1008, "委托人已注册"),
    TRUSTEE_NOTEXISTS(1009, "委托人不存在"),
    VOTER_EXISTS(1010, "投票人已注册"),
    VOTER_NOTEXISTS(1011, "投票人不存在"),
    WALLET_BACKUP_EXCEP(1012,"备份钱包文件异常"),
    WALLET_IMPORT_EXCEP(1013,"导入钱包文件异常"),
    WALLET_PWD_ERROR(1014,"钱包密码错误"),
    WALLET_ACCOUNT_EXISTS(1015,"账户已存在"),
    //***交易***
    TRANS_UNCOMPSYN(1016,"未同步完成");

    private int code;
    private String message;//自定义异常描述
    private String originalInfo;//程序抛出异常信息

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
