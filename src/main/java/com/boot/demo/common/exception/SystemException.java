package com.boot.demo.common.exception;

/**
 * <p>
 * 系统异常拦截
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月23日
 */
public class SystemException extends CommonException {
    public SystemException(Integer code, String message, Exception e) {
        super(code, message, e);
    }

    public SystemException(Exception e) {
        super(e);
    }
}
