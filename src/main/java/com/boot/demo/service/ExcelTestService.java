/**
 * @filename:SystemDictionaryService 2019-07-13 03:57:11
 * @project ydsh-saas-service-basis  V1.0
 * Copyright(c) 2020 <a href=mailto:yangyanrui@yidianlife.com>xiaoyang</a> Co. Ltd.
 * All right reserved.
 */
package com.boot.demo.service;


import com.boot.demo.common.utils.ExcelUtil;
import com.boot.demo.common.utils.JsonResult;
import com.boot.demo.entity.test;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 内部api接口层（被其他服务调用）
 * </p>
 *
 * @author <a href="mailto:yangyanrui@yidianlife.com">xiaoyang</a>
 * @version V0.0.1
 * @date 2019年08月23日
 */
@RequestMapping(value = {"/excel"})
public interface ExcelTestService {

    /**
     * 测试
     *
     * @param file
     * @param response
     * @return
     */
    @PostMapping(value = "/test")
    default JsonResult excel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        List<test> list = ExcelUtil.importExcel(file, test.class);
        return new JsonResult().success(list);
//        Map<String, Object> tests = new HashMap<>();
//        tests.put("id", 134654654654654654L);
//        tests.put("type", "ceshi");
//        tests.put("name", "leixing");
//        List<Map<String, Object>> list = new ArrayList<>();
//        list.add(tests);
//        System.out.println(ExcelUtil.exportExcel("测试", test.class, list, response));
//        return null;
    }
}