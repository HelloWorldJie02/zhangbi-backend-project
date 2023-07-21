package com.zhang.bi.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * excel 工具类
 * @author ZHANG
 */
@Slf4j
public class ExcelUtils {
    /**
     *转CSV
     */
    public static String excelToCsv(MultipartFile multipartFile) {
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("格式转换失败", e);
        }
        if(CollectionUtil.isEmpty(list)){
            return "";
        }
        //转csv
        StringBuilder stringBuilder = new StringBuilder();
        //取表头
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap<Integer, String>) list.get(0);
        List<String> headList = headMap.values().stream().
                filter(ObjectUtil::isNotEmpty).
                collect(Collectors.toList());
        stringBuilder.append(CollectionUtil.join(headList, ",")).append("\n");
        //取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap.values().stream().
                    filter(ObjectUtil::isNotEmpty).
                    collect(Collectors.toList());
            stringBuilder.append(CollectionUtil.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }
}
