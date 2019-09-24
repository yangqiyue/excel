package com.boot.demo.common.enums;

/**
 * <p>
 * 小数点格式
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月20日
 */
public enum DecimalType {

    /**
     * 一位
     */
    one(1, "0.0"),
    /**
     * 两位
     */
    two(2, "0.00"),
    /**
     * 三位
     */
    three(3, "0.000"),
    /**
     * 四位
     */
    four(4, "0.0000"),
    /**
     * 五位
     */
    five(5, "0.00000");

    /**
     * 日期格式
     */
    private String decimal;

    private int scale;

    /**
     * 日期格式
     *
     * @param scale
     * @param decimal
     */
    DecimalType(int scale, String decimal) {
        this.scale = scale;
        this.decimal = decimal;
    }

    /**
     * 获取日期格式
     *
     * @return
     */
    public String getDecimal() {
        return decimal;
    }

    /**
     * 获取日期格式
     *
     * @return
     */
    public int getScale() {
        return scale;
    }
}
