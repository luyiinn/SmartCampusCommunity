package com.dewmark.smartcampuscommunity;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class SmartCampusCommunityApplicationTests {

    @Autowired
    private ReactAgent copywritingAgent;
    @Test
    void contextLoads() {
        String prompt = String.format("""
            请优化以下文案：
           
            原文：
            %s
            
            要求：
            1. 保持核心信息不变
            2. 提升吸引力
            3. 优化可读性
            """, "今天天气真不错");
        try {
            Optional<OverAllState> result = copywritingAgent.invoke(prompt);
// 打印整个 OverAllState 的内容（关键！）
            if (result.isPresent()) {
                System.out.println("=== OverAllState 内容 ===");
                System.out.println(result.get().toString());
            } else {
                System.out.println("智能体未返回有效状态");
            }

// 然后再尝试提取响应
            String response = result
                    .map(OverAllState::data)
                    .map(data -> "data: " + data) // 或者直接处理
                    .orElse("未获得到响应");

            System.out.println("最终响应: " + response);
        } catch (GraphRunnerException e) {
            throw new RuntimeException("文案优化失败：" + e.getMessage(), e);
        }
    }

}
