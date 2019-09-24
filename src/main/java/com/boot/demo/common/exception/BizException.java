package com.boot.demo.common.exception;

/**
 * <p>
 * 业务异常拦截
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月23日
 */
public class BizException extends CommonException {
    public BizException(Integer code, String message) {
        super(code, message);
    }

    public BizException(Integer code, String message, Exception e) {
        super(code, message, e);
    }
}
