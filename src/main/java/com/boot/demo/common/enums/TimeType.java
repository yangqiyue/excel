package com.boot.demo.common.enums;

/**
 * <p>
 * 日期格式
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月20日
 */
public enum TimeType {

    /**
     * yyyy-MM-dd
     */
    DATE_FORMAT("yyyy-MM-dd"),
    /**
     * yyyy-MM
     */
    YEAR_S_MONTH("yyyy-MM"),
    /**
     * yyyyMM
     */
    YEAR_MONTH("yyyyMM"),
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    TIMEF_FORMAT("yyyy-MM-dd HH:mm:ss"),
    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     */
    MSEL_FORMAT("yyyy-MM-dd HH:mm:ss.SSS"),
    /**
     * yyyy年MM月dd日
     */
    ZHCN_DATE_FORMAT("yyyy年MM月dd日"),
    /**
     * yyyy年MM月dd日HH时mm分ss秒
     */
    ZHCN_TIME_FORMAT("yyyy年MM月dd日HH时mm分ss秒"),
    /**
     * yyyy年MM月dd日HH时mm分ss秒SSS毫秒
     */
    ZHCN_MSEL_FORMAT("yyyy年MM月dd日HH时mm分ss秒SSS毫秒"),
    /**
     * yyyyMMdd
     */
    DATE_STR_FORMAT("yyyyMMdd"),
    /**
     * yyyyMMddHHmmss
     */
    TIME_STR_FORMAT("yyyyMMddHHmmss"),
    /**
     * yyyyMMddHHmmssSSS
     */
    MSEL_STR_FORMAT("yyyyMMddHHmmssSSS"),
    /**
     * yyyy-MM-dd HH:mm
     */
    MSEL_MIU_FORMAT("yyyy-MM-dd HH:mm"),
    /**
     * yyyyMMddHH
     */
    MS_MIU_FORMAT("yyyyMMddHH");

    /**
     * 日期格式
     */
    private String timeType;

    /**
     * 日期格式
     *
     * @param timeType
     */
    TimeType(String timeType) {
        this.timeType = timeType;
    }

    /**
     * 获取日期格式
     *
     * @return
     */
    public String getTimeType() {
        return timeType;
    }


}
