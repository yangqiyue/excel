package com.boot.demo.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.boot.demo.common.annotation.Excel;
import com.boot.demo.common.enums.ErrorCode;
import com.boot.demo.common.exception.BizException;
import com.boot.demo.common.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * excel 工具类
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年09月12日
 */
@Slf4j
public class ExcelUtil {
    /**
     * xls 后缀
     */
    private final static String XLS = "xls";
    /**
     * xlsx 后缀
     */
    private final static String XLS_X = "xlsx";
    /**
     * 列不对等
     */
    private final static String ROW_NUM_ERROR = "列不对等！";
    /**
     * 文件不存在
     */
    private final static String FILE_NOT_ERROR = "文件不存在！";
    /**
     * 表头错误
     */
    private final static String NAME_ERROR = "表头错误！";
    /**
     * 表头错误
     */
    private final static String ANNOTATION_ERROR = "注解空异常！";
    /**
     * 实体空异常
     */
    private final static String BEAN_ERROR = "实体空异常！";
    /**
     * 科学计数
     */
    private final static String E = "e";


    /**
     * 传入文本对象输出list集合（导入）
     *
     * @param file  流文件
     * @param clazz 要转义成的类对象
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) {
        // 检查文件
        checkFile(file);
        // 获得HSSFWorkbook工作薄对象
        Workbook workbook = getWorkBook(file);
        List<T> list = new ArrayList<T>();
        //获取对象总数量
        Field[] fields = getSortFields(clazz);
        //对象字段排序
        Arrays.sort(fields, (a, b) -> {
            return a.getAnnotation(Excel.class).orderNum() - b.getAnnotation(Excel.class).orderNum();
        });
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                // 获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null || sheet.getLastRowNum() == 0) {
                    continue;
                }
                // 获取当前sheet工作表的列总数
                int firstLine = sheet.getRow(0).getPhysicalNumberOfCells();
                if (fields.length != firstLine) {
                    throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), ROW_NUM_ERROR);
                }
                // 获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                // 获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                // 循环所有行
                for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                    // 获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    Object obj;
                    try {
                        obj = clazz.newInstance();
                    } catch (IllegalAccessException e) {
                        log.error("【excel导入】clazz映射地址：{},{}", clazz.getCanonicalName(), "excel导入异常！");
                        throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel导入异常", e);
                    } catch (InstantiationException e) {
                        log.error("【excel导入】clazz映射地址：{},{}", clazz.getCanonicalName(), "excel导入异常！");
                        throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel导入异常", e);
                    }
                    for (int cellNum = 0; cellNum < firstLine; cellNum++) {
                        // 取出对应注解
                        Excel excel = fields[cellNum].getAnnotation(Excel.class);
                        Cell cell = row.getCell(cellNum);
                        if (rowNum == 0) {
                            // 第一行 判断表头名称
                            if (cell == null || StringUtils.isEmpty(cell.getStringCellValue()) || !cell.getStringCellValue().equals(excel.titleName())) {
                                throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), NAME_ERROR);
                            }
                            continue;
                        }
                        Object value = getCellValue(cell);
                        // 判断注解是否允许空值
                        if (!excel.empty()) {
                            if (value == null || "".equals(value)) {
                                throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), excel.titleName() + "不能为空");
                            }
                        }
                        // 根绝类型 实体类赋值
                        createBean(fields[cellNum], obj, value);
                    }
                    if (rowNum == 0) {
                        // 表头不做记录
                        continue;
                    }
                    list.add((T) obj);
                }
            }
        }
        return list;
    }

    /**
     * 导出模版
     *
     * @param excelName excel 名称
     * @param clazz     数据集
     * @param response  使用response可以导出到浏览器
     * @param <T>
     * @return
     */
    public static <T> Boolean exportTemplate(String excelName, Class<T> clazz, HttpServletResponse response) {
        return exportExcel(excelName, null, clazz, Type.XLS, response, false);
    }

    /**
     * 导出模版
     *
     * @param excelName excel 名称
     * @param clazz     数据集
     * @param type      excel 类型
     * @param response  使用response可以导出到浏览器
     * @param <T>
     * @return
     */
    public static <T> Boolean exportTemplate(String excelName, Class<T> clazz, Type type, HttpServletResponse response) {
        return exportExcel(excelName, null, clazz, type, response, false);
    }

    /**
     * excel 导出 （对象）
     *
     * @param excelName excel 名称
     * @param list      数据集
     * @param clazz     反射clazz
     * @param response  使用response可以导出到浏览器
     * @param <T>
     * @return
     */
    public static <T> Boolean exportExcel(String excelName, List<T> list, Class<T> clazz, HttpServletResponse response) {
        return exportExcel(excelName, list, clazz, Type.XLS, response, true);
    }

    /**
     * excel 导出 （对象）
     *
     * @param excelName excel 名称
     * @param list      数据集
     * @param clazz     反射clazz
     * @param type      excel 类型
     * @param response  使用response可以导出到浏览器
     * @param <T>
     * @return
     */
    public static <T> Boolean exportExcel(String excelName, List<T> list, Class<T> clazz, Type type, HttpServletResponse response) {
        return exportExcel(excelName, list, clazz, type, response, true);
    }

    /**
     * excel 导出 （Map）
     *
     * @param excelName excel 名称
     * @param clazz     反射clazz
     * @param list      数据集
     * @param response  使用response可以导出到浏览器
     * @param <T>
     * @return
     */
    public static <T> Boolean exportExcel(String excelName, Class<T> clazz, List<Map<String, Object>> list, HttpServletResponse response) {
        return exportExcel(excelName, clazz, list, Type.XLS, response, true);
    }

    /**
     * excel 导出 （Map）
     *
     * @param excelName excel 名称
     * @param clazz
     * @param list      数据集
     * @param type      excel 类型
     * @param response  使用response可以导出到浏览器
     * @param <T>
     * @return
     */
    public static <T> Boolean exportExcel(String excelName, Class<T> clazz, List<Map<String, Object>> list, Type type, HttpServletResponse response) {
        return exportExcel(excelName, clazz, list, type, response, false);
    }

    /**
     * excel 导出 （Map）
     *
     * @param excelName excel 名称
     * @param clazz
     * @param list      数据集
     * @param type      excel 类型
     * @param response  使用response可以导出到浏览器
     * @param flag      true：数据导出 false：模版导出
     * @param <T>
     * @return
     */
    private static <T> Boolean exportExcel(String excelName, Class<T> clazz, List<Map<String, Object>> list, Type type, HttpServletResponse response, boolean flag) {
        if (list == null || list.size() == 0) {
            log.error("【excel导出】{}", "excel导出数据空异常！");
            return false;
        }
        List<T> ts = JSONArray.parseArray(JSON.toJSONString(list), clazz);
        return exportExcel(excelName, ts, clazz, type, response, flag);
    }

    /**
     * excel 导出 （对象）
     *
     * @param excelName excel 名称
     * @param list      数据集
     * @param clazz     反射clazz
     * @param type      excel 类型
     * @param response  使用response可以导出到浏览器
     * @param flag      true：数据导出 false：模版导出
     * @param <T>
     * @return
     */
    private static <T> Boolean exportExcel(String excelName, List<T> list, Class<T> clazz, Type type, HttpServletResponse response, boolean flag) {
        if (flag) {
            // 非模版导出，判断数据是否为空！
            if (list == null || list.size() == 0) {
                log.error("【excel导出】{}", "excel导出数据空异常！");
                return false;
            }
        }
        // 设置默认文件名为当前时间：年月日时分秒
        if (StringUtils.isEmpty(excelName)) {
            log.info("【excel导出】{}", "excel导出未设置文件名，默认使用时间戳代替！");
            excelName = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        }
        createResponse(excelName, response, type);
        //获取对象总数量
        Field[] fields = getSortFields(clazz);
        Workbook workbook;
        switch (type) {
            case XLS:
                workbook = new HSSFWorkbook();
                break;
            case XLS_X:
                workbook = new XSSFWorkbook();
                break;
            default:
                log.error("【excel导出】{}", "excel类型错误，只支持xls与xlsx！");
                return false;
        }
        // 创建一个工作表sheet 默认是表名是sheet0
        Sheet sheet = workbook.createSheet(excelName);
        CellStyle cellStyle = getCellStyle(workbook);
        setWorkBook(workbook, cellStyle, sheet, fields);
        // CellStyle 缓存
        Map<String, CellStyle> hashMap = new HashMap<>();
        try {
            if (flag) {
                // 开始生成excel
                for (int rowIndex = 1; rowIndex <= list.size(); rowIndex++) {
                    Object obj = list.get(rowIndex - 1);
                    Field[] sortFields = getSortFields(obj.getClass());
                    //创建第 rowIndex 行）
                    Row row = sheet.createRow(rowIndex);
                    for (int i = 0; i < sortFields.length; i++) {
                        Field field = sortFields[i];
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        Object o = new PropertyDescriptor(field.getName(), clazz).getReadMethod().invoke(obj);
                        if (!field.getAnnotation(Excel.class).empty() && o == null) {
                            log.error("【excel导出】class映射地址：{},空指针参数：{},{}", clazz.getCanonicalName(), field.getName(), "数据集空指针");
                            return false;
                        }
                        setValue(getCell(workbook, hashMap, row, i, o, field), o, field);
                    }
                }
            }
            //将文件输出
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel导出异常", e);
        } catch (IllegalAccessException e) {
            throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel导出异常", e);
        } catch (InvocationTargetException e) {
            throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel导出异常", e);
        } catch (IntrospectionException e) {
            throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel导出异常", e);
        }
    }


//    ---------------------------------------------------- excel 工具 ------------------------------------------------------------------


    /**
     * 设置表格内容的值
     *
     * @param cell  单元格对象
     * @param value 单元格的值
     */
    private static void setValue(Cell cell, Object value, Field field) {
        if (value == null || "".equals(value)) {
            return;
        } else if (value instanceof String) {
            cell.setCellValue(value.toString());
        } else if (value instanceof Integer
                || value instanceof Double
                || value instanceof Float
                || value instanceof Long
                || value instanceof Short) {
            if (field.getAnnotation(Excel.class).type().IsMoney()) {
                // 判断类型
                BigDecimal bi1 = new BigDecimal(value.toString());
                BigDecimal bi2 = new BigDecimal("10000");
                BigDecimal divide = bi1.divide(bi2, field.getAnnotation(Excel.class).type().decimalType().getScale(), RoundingMode.HALF_UP);
                cell.setCellValue(divide.doubleValue());
            } else {
                cell.setCellValue(value.toString());
            }
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(field.getAnnotation(Excel.class).type().timeType().getTimeType());
            cell.setCellValue(sdf.format((Date) value));
        }
    }

    /**
     * 设置excel单元格样式
     *
     * @param workbook
     * @param hashMap
     * @param row
     * @param num
     * @param value
     * @param field
     * @return
     */
    private static Cell getCell(Workbook workbook, Map<String, CellStyle> hashMap, Row row, int num, Object value, Field field) {
        CellStyle cellStyle;
        // 获取指定单元格
        Cell cell = row.createCell(num);
        // 设置类型
        DataFormat format = workbook.createDataFormat();
        if (value instanceof Integer
                || value instanceof Double
                || value instanceof Float
                || value instanceof Long) {
            if (field.getAnnotation(Excel.class).type().IsMoney()) {
                cellStyle = hashMap.get(field.getAnnotation(Excel.class).type().decimalType().getDecimal().toString());
                if (cellStyle == null) {
                    cellStyle = getCellStyle(workbook);
                    cellStyle.setDataFormat(format.getFormat(field.getAnnotation(Excel.class).type().decimalType().getDecimal().toString()));
                    hashMap.put(field.getAnnotation(Excel.class).type().decimalType().getDecimal().toString(), cellStyle);
                }
            } else {
                cellStyle = hashMap.get("@");
                if (cellStyle == null) {
                    cellStyle = getCellStyle(workbook);
                    cellStyle.setDataFormat(format.getFormat("@"));
                    hashMap.put("@", cellStyle);
                }
            }
        } else if (value instanceof Date) {
            cellStyle = hashMap.get(field.getAnnotation(Excel.class).type().timeType().getTimeType().toString());
            if (cellStyle == null) {
                cellStyle = getCellStyle(workbook);
                cellStyle.setDataFormat(format.getFormat(field.getAnnotation(Excel.class).type().timeType().getTimeType()));
                hashMap.put(field.getAnnotation(Excel.class).type().timeType().getTimeType().toString(), cellStyle);
            }
        } else {
            cellStyle = hashMap.get("@");
            if (cellStyle == null) {
                cellStyle = getCellStyle(workbook);
                cellStyle.setDataFormat(format.getFormat("@"));
                hashMap.put("@", cellStyle);
            }
        }
        cell.setCellStyle(cellStyle);
        return cell;
    }

    /**
     * 设置excel 样式 （第一行格式）
     *
     * @param workbook
     * @param cellStyle
     * @param sheet
     * @param fields
     */
    private static void setWorkBook(Workbook workbook, CellStyle cellStyle, Sheet sheet, Field[] fields) {
        //写入excel的表头（创建第一行）
        Row row = sheet.createRow(0);
        // 设置类型
        DataFormat format = workbook.createDataFormat();
        // 设置列宽、表头、数据类型
        for (int i = 0; i < fields.length; i++) {
            //设置宽度
            sheet.setColumnWidth(i, fields[i].getAnnotation(Excel.class).titleSize() * 256);
            //创建第一行
            Cell cell = row.createCell(i);
            //设置表头名称
            cell.setCellValue(fields[i].getAnnotation(Excel.class).titleName());
            cell.setCellStyle(cellStyle);
            cellStyle.setDataFormat(format.getFormat("@"));
            sheet.setDefaultColumnStyle(i, cellStyle);
        }
    }

    /**
     * 初始化样式属性
     *
     * @param workbook
     * @return
     */
    private static CellStyle getCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置对齐方式为居中对齐
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置自动换行
        cellStyle.setWrapText(true);
        // 设置单元格内容垂直对其方式为居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置字体
//        Font font = workbook.createFont();
//        font.setFontName("宋体");
//        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 创建excel 导出 response信息
     *
     * @param excelName
     * @param response
     */
    private static void createResponse(String excelName, HttpServletResponse response, Type type) {
        // 设置response头信息
        //        response.reset();
        // 改成输出excel文件
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
        try {
            switch (type) {
                case XLS:
                    response.setHeader("Content-disposition", "attachment; filename="
                            + new String(URLEncoder.encode(excelName, "UTF-8").getBytes("UTF-8"), "ISO8859-1") + ".xls");
                    break;
                case XLS_X:
                    response.setHeader("Content-disposition", "attachment; filename="
                            + new String(URLEncoder.encode(excelName, "UTF-8").getBytes("UTF-8"), "ISO8859-1") + ".xlsx");
                    break;
                default:
                    log.error("【excel导出】{}", "excel类型错误，只支持xls与xlsx！");
                    throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), "excel类型错误，只支持xls与xlsx！");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("【excel导出】{}", "设置response信息异常！");
            throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "设置response信息异常！", e);
        }
    }

    /**
     * 根据实体类型 赋值数据
     *
     * @param field
     * @param newInstance
     * @param value
     * @param <T>
     */
    private static <T> void createBean(Field field, T newInstance, Object value) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            if (value == null) {
                field.set(newInstance, null);
            } else if (Long.class.equals(field.getType())) {
                field.set(newInstance, Long.valueOf(String.valueOf(value)));
            } else if (String.class.equals(field.getType())) {
                field.set(newInstance, String.valueOf(value));
            } else if (Integer.class.equals(field.getType())) {
                field.set(newInstance, Integer.valueOf(String.valueOf(value)));
            } else if (int.class.equals(field.getType())) {
                field.set(newInstance, Integer.parseInt(String.valueOf(value)));
            } else if (Date.class.equals(field.getType())) {
                field.set(newInstance, (Date) value);
            } else if (Boolean.class.equals(field.getType())) {
                field.set(newInstance, (Boolean) value);
            } else if (Double.class.equals(field.getType())) {
                field.set(newInstance, Double.valueOf(String.valueOf(value)));
            } else {
                field.set(newInstance, value);
            }
        } catch (IllegalAccessException e) {
            log.error("【excel导入】clazz映射地址：{},{},{}", newInstance, "excel实体赋值类型转换异常！", e);
            throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel实体赋值类型转换异常", e);
        }
    }

    /**
     * 实体判空，注解判空
     *
     * @param clazz
     * @return
     */
    private static Field[] getSortFields(Class clazz) {
        //获取对象总数量
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            log.error("【excel导入】clazz映射地址：{},{}", clazz.getCanonicalName(), "实体空异常！");
            throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), BEAN_ERROR);
        }
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Excel.class)) {
                log.error("【excel导入】clazz映射地址：{},{}", clazz.getCanonicalName(), "实体空Excel注解异常！");
                throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), ANNOTATION_ERROR);
            }
        }
        Arrays.sort(fields, (a, b) -> {
            return a.getAnnotation(Excel.class).orderNum() - b.getAnnotation(Excel.class).orderNum();
        });
        return fields;
    }

    /**
     * 列转化值
     *
     * @param cell 列值
     * @throws IOException
     */
    private static Object getCellValue(Cell cell) {
        Object cellValue = null;
        if (cell == null) {
            return cellValue;
        }

        // 把数字当成String来读，避免出现1读成1.0的情况
        // 判断数据的类型
        switch (cell.getCellType()) {
            case NUMERIC:
                if (cell.getCellType() == CellType.NUMERIC) {
                    if (DateUtil.isValidExcelDate(cell.getNumericCellValue())) {
                        CellStyle style = cell.getCellStyle();
                        if (style == null) {
                            return false;
                        }
                        int i = style.getDataFormat();
                        String f = style.getDataFormatString();
                        boolean isDate = DateUtil.isADateFormat(i, f);
                        if (isDate) {
                            Date date = cell.getDateCellValue();
                            return cellValue = date;
                        }
                    }
                }
                // 防止科学计数进入
                if (String.valueOf(cell.getNumericCellValue()).toLowerCase().contains(E)) {
                    throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), "excel数据类型错误，请将数字转文本类型！！");
                }
                if ((int) cell.getNumericCellValue() != cell.getNumericCellValue()) {
                    // double 类型
                    cellValue = cell.getNumericCellValue();
                } else {
                    cellValue = (int) cell.getNumericCellValue();
                }
                break;
            // 字符串
            case STRING:
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            // Boolean
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            // 公式
            case FORMULA:
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            // 空值
            case BLANK:
                cellValue = null;
                break;
            // 故障
            case ERROR:
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    /**
     * 由文件生成 poi Workbook
     *
     * @param file
     * @return
     */
    private static Workbook getWorkBook(MultipartFile file) {
        // 获得文件名
        String fileName = file.getOriginalFilename();
        // 创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        // 获取excel文件的io流
        InputStream is = null;
        try {
            is = file.getInputStream();
            // 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(XLS)) {
                // 2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(XLS_X)) {
                // 2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            throw new SystemException(ErrorCode.SYS_EXCEPTION.getCode(), "excel 转换 HSSFWorkbook 异常！", e);
        }
        return workbook;
    }

    /**
     * 检查文件
     *
     * @param file
     * @throws IOException
     */
    private static void checkFile(MultipartFile file) {
        // 判断文件是否存在
        if (null == file) {
            throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), FILE_NOT_ERROR);
        }
        // 获得文件名
        String fileName = file.getOriginalFilename();
        // 判断文件是否是excel文件
        if (!fileName.endsWith(XLS) && !fileName.endsWith(XLS_X)) {
            throw new BizException(ErrorCode.SYS_EXCEPTION.getCode(), fileName + "不是excel文件");
        }
    }
}

