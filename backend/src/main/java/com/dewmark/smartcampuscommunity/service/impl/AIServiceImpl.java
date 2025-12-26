package com.dewmark.smartcampuscommunity.service.impl;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.dewmark.smartcampuscommunity.constent.MessageConstant;
import com.dewmark.smartcampuscommunity.exception.DataNotIlegalException;
import com.dewmark.smartcampuscommunity.service.AIService;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private ReactAgent copywritingAgent;

    @Override
    public String copywrite(String originalText) {
        if (originalText == null || originalText.isEmpty()) {
            throw new DataNotIlegalException(MessageConstant.DATA_UNILEGAL);
        }

        String prompt = String.format("""
            请优化以下文案：

            原文：
            %s

            要求：
            1. 保持核心信息不变
            2. 提升吸引力
            3. 优化可读性
            """, originalText);

        try {
            Optional<OverAllState> result = copywritingAgent.invoke(prompt);

            if (result.isEmpty()) {
                throw new RuntimeException("智能体未返回有效响应");
            }

            OverAllState state = result.get();
            Object dataObj = state.data();

            if (!(dataObj instanceof Map)) {
                throw new RuntimeException("响应数据格式异常：data 不是 Map 类型");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;

            Object messagesObj = data.get("messages");
            if (!(messagesObj instanceof List)) {
                throw new RuntimeException("响应中缺少有效的 messages 列表");
            }

            @SuppressWarnings("unchecked")
            List<?> messages = (List<?>) messagesObj;

            for (Object msgObj : messages) {
                if (msgObj instanceof AssistantMessage assistantMsg) {
                    String text = assistantMsg.getText(); // ✅ 正确方法（M5）
                    return (text != null) ? text.trim() : "";
                }
            }

            throw new RuntimeException("未找到 AI 助手的回复");

        } catch (Exception e) {
            throw new RuntimeException("文案优化失败：" + e.getMessage(), e);
        }
    }
}