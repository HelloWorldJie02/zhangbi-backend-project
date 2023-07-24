package com.zhang.bi.model.vo;

import lombok.Data;

@Data
public class BiResponse {

    /**
     * 图标id
     */
    private Long chartId;

    /**
     * 生成的图表信息
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;
}
