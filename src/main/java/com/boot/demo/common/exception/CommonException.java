package com.boot.demo.common.exception;

/**
 * <p>
 * 异常拦截
 * </p>
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月23日
 */
public class CommonException extends RuntimeException {
    protected Integer code;

    public Integer getCode() {
        return code;
    }

    protected CommonException(Integer code, String message, Exception e) {
        super(message, e);
        this.code = code;
    }

    protected CommonException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    protected CommonException(Exception e) {
        super(e);
    }
}
