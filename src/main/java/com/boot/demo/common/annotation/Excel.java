package com.boot.demo.common.annotation;


import com.boot.demo.common.enums.DecimalType;
import com.boot.demo.common.enums.TimeType;

import java.lang.annotation.*;

/**
 * <p>
 * excel 注解
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月10日
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Excel {

    /**
     * 表头中文
     *
     * @return
     */
    String titleName();

    /**
     * 列宽
     *
     * @return
     */
    int titleSize() default 30;

    /**
     * 字段顺序  正序
     *
     * @return
     */
    int orderNum();

    /**
     * 是否允许空值 ，默认不允许
     * <p>
     * false：不允许   true ：允许
     *
     * @return
     */
    boolean empty() default false;

    /**
     * 内部类
     *
     * @return
     */
    CellType type() default @CellType;

    /**
     * 设置格式
     * 默认：
     * 时间：yyyy-MM-dd HH:mm:ss
     * 小数点：两位，四舍五入
     *
     * @return
     */
    @interface CellType {

        TimeType timeType() default TimeType.TIMEF_FORMAT;

        DecimalType decimalType() default DecimalType.two;
    }
}
