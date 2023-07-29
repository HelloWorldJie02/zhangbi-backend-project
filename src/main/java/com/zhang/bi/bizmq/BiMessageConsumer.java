package com.zhang.bi.bizmq;

import com.rabbitmq.client.Channel;
import com.zhang.bi.common.ErrorCode;
import com.zhang.bi.constant.CommonConstant;
import com.zhang.bi.exception.BusinessException;
import com.zhang.bi.manager.AiManager;
import com.zhang.bi.model.entity.Chart;
import com.zhang.bi.model.enums.ChartStatusEnum;
import com.zhang.bi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;


    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.info("Receive message : {}", message);
        try {
            if(StringUtils.isBlank(message)){
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入ID为空");
            }
            long chartId = Long.parseLong(message);
            Chart chart = chartService.getById(chartId);
            if(ObjectUtils.anyNull(chart)){
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图表信息不存在");
            }
            //开始处理信息，将图表状态改为执行中
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setChartStatus(ChartStatusEnum.RUNNING.getValue());
            boolean b = chartService.updateById(updateChart);
            if(!b){
                //更新图标状态失败，拒绝消息
                channel.basicNack(deliveryTag, false, false);
                Chart updateChartFailed = new Chart();
                updateChartFailed.setId(chart.getId());
                updateChartFailed.setChartStatus(ChartStatusEnum.FAILED.getValue());
                chartService.updateById(updateChartFailed);
                chartService.handleChartUpdateError(chart.getId(), "更新图表·执行中状态·失败");
                return;
            }
            //提交Ai处理
            String answer = aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(chart));
            String[] splits = answer.split("【【【【【");
            if( splits.length != 3){
                //将消息返回队列重新生成
                channel.basicNack(deliveryTag, false, true);
                chartService.handleChartUpdateError(chart.getId(), "AI生成错误");
                return;
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();

            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setChartStatus(ChartStatusEnum.SUCCEED.getValue());
            boolean updateResult = chartService.updateById(updateChartResult);
            if(!updateResult){
                //拒绝
                channel.basicNack(deliveryTag, false, false);
                Chart updateChartFailed = new Chart();
                updateChartFailed.setId(chart.getId());
                updateChartFailed.setChartStatus(ChartStatusEnum.FAILED.getValue());
                chartService.updateById(updateChartFailed);
                chartService.handleChartUpdateError(chart.getId(), "更新图标'成功状态'失败");
            }
            //成功，确认消息
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建用户的输入信息
     *
     * @param chart
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String chartData = chart.getChartData();

        // 无需Prompt，直接调用现有模型
        //拼接用户输入
        StringBuilder userInput = new StringBuilder();
        String usergoal = goal;
        if(StringUtils.isNotBlank(chartType)){
            usergoal = goal + ",并使用" + chartType;
        }
        userInput.append("分析目标：").append("\n")
                .append(usergoal).append("\n")
                .append("原始数据：").append("\n")
                .append(chartData).append("\n");
        return userInput.toString();
    }
}
