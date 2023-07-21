package com.zhang.bi.model.dto.chart;

import lombok.Data;

@Data
public class GenChartByAiRequest {

    /**
     * 图标名称
     */
    private String chartName;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;
}
