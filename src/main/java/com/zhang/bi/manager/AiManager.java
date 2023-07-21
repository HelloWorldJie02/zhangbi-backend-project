package com.zhang.bi.manager;

import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import com.zhang.bi.common.ErrorCode;
import com.zhang.bi.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AiManager {
    @Resource
    private YuCongMingClient yuCongMingClient;

    public String doChat(long modelId, String message){
        // 构造请求
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);

        // 获取响应
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if(response == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI回复失败，请重试");
        }
        return response.getData().getContent();
    }
}
