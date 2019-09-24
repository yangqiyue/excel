package com.boot.demo.common.enums;

/**
 * <p>
 * 异常
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月23日
 */
public enum ErrorCode {
    /**
     * 系统异常,操作失败
     */
    SYS_EXCEPTION(9000, "系统异常,操作失败"),
    /**
     * 参数非法
     */
    ILLEGAL_ARGUMENT(9001, "参数非法"),
    /**
     * 参数校验不通过
     */
    ARGUMENT_NOT_VALID(9002, "参数校验不通过");

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsgByCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (code.equals(errorCode.getCode())) {
                return errorCode.getMsg();
            }
        }
        return null;
    }
}
