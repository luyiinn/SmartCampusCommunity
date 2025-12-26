package com.dewmark.smartcampuscommunity.config.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:智能体配置类
 * @author: dewMark
 * @date: 2025/12/25
 **/
@Data
@Configuration
public class AgentConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;


    @Bean
    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(apiKey) // 此时 apiKey 已被注入
                .build();
    }

    @Bean
    public ChatModel chatModel(DashScopeApi dashScopeApi) {
        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();
    }



    @Bean
    public ReactAgent copywritingAgent(ChatModel chatModel) {

        String systemPrompt = """
        你是一个专业的文案优化专家。
        请遵循以下规则：
        1. 保持原意不变
        2. 优化表达流畅度
        3. 符合目标读者群体（学生）
        4. 适当加入情感元素
        5. 不可生成违规内容
        6. 根据原文案内容决定优化后的风格和特色，如应用型文案应确保严谨、分享型文案应具有活力和优美
        7. 遵循原文的叙述人称选择
        8. 避免使用markdown语法
        9. 合理的分段落叙述
        
        """;

        return ReactAgent.builder()
                .name("copywriting_agent")
                .description("优化文案的智能体")
                .model(chatModel)
                .systemPrompt(systemPrompt)
                .build();
    }
}
