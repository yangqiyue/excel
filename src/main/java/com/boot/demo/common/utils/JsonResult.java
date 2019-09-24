package com.boot.demo.common.utils;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.net.ConnectException;
import java.sql.SQLException;

/**
 * <p>
 * 统一出参包装类
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月23日
 */
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1559840165163L;
    private Integer code;
    private String message;
    private T data;

    public JsonResult() {
    }

    public JsonResult(Integer code, String operate, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public JsonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResult<T> success(String message, T data) {
        this.setCode(Const.CODE_SUCCESS);
        this.setMessage(message);
        this.setData(data);
        return this;
    }

    public JsonResult<T> success(T data) {
        this.setCode(Const.CODE_SUCCESS);
        this.setMessage("操作成功");
        this.setData(data);
        return this;
    }

    public JsonResult<T> success(String message) {
        this.setCode(Const.CODE_SUCCESS);
        this.setMessage(message);
        this.setData(null);
        return this;
    }

    public JsonResult<T> error(String message) {
        this.setCode(Const.CODE_FAILED);
        this.setMessage(message);
        this.setData(null);
        return this;
    }

    public JsonResult<T> error(String message, T data) {
        this.setCode(Const.CODE_FAILED);
        this.setMessage(message);
        this.setData(data);
        return this;
    }

    public static JsonResult fail() {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setCode(Const.CODE_FAILED);
        jsonResult.setMessage("操作失败");
        return jsonResult;
    }

    public JsonResult(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            this.code = 1001;
            this.message = "空指针：" + throwable;
        } else if (throwable instanceof ClassCastException) {
            this.code = 1002;
            this.message = "类型强制转换异常：" + throwable;
        } else if (throwable instanceof ConnectException) {
            this.code = 1003;
            this.message = "链接失败：" + throwable;
        } else if (throwable instanceof IllegalArgumentException) {
            this.code = 1004;
            this.message = "传递非法参数异常：" + throwable;
        } else if (throwable instanceof NumberFormatException) {
            this.code = 1005;
            this.message = "数字格式异常：" + throwable;
        } else if (throwable instanceof IndexOutOfBoundsException) {
            this.code = 1006;
            this.message = "下标越界异常：" + throwable;
        } else if (throwable instanceof SecurityException) {
            this.code = 1007;
            this.message = "安全异常：" + throwable;
        } else if (throwable instanceof SQLException) {
            this.code = 1008;
            this.message = "数据库异常：" + throwable;
        } else if (throwable instanceof ArithmeticException) {
            this.code = 1009;
            this.message = "算术运算异常：" + throwable;
        } else if (throwable instanceof RuntimeException) {
            this.code = 1010;
            this.message = "运行时异常：" + throwable;
        } else if (throwable instanceof Exception) {
            this.code = 9999;
            this.message = "未知异常" + throwable;
        }

    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public JsonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功code=0
     *
     * @return true/false
     */
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return Const.CODE_SUCCESS.equals(this.code);
    }

    /**
     * 失败
     *
     * @return true/false
     */
    @JSONField(serialize = false)
    public boolean isFail() {
        return !isSuccess();
    }
}
