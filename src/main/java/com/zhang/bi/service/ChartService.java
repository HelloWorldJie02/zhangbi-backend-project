package com.zhang.bi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhang.bi.model.dto.chart.ChartQueryRequest;
import com.zhang.bi.model.entity.Chart;

/**
* @author ZHANG
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-07-17 16:11:39
*/
public interface ChartService extends IService<Chart> {

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

    /**
     * 统一处理失败状态
     * @param chartId
     * @param execMessage
     */
    void handleChartUpdateError(long chartId, String execMessage);
}
