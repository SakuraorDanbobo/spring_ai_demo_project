package com.danbobo.springai.deepseekdemo.Advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * @author Danbobo
 * @Description
 * @date 2025/8/20 15:25
 */
public class MyChatReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_USER_TEXT_ADVISE = """
            {pre_input_query}
            add again input query : {pre_input_query}
            """;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {

        // 获取原提示词
        String contents = chatClientRequest.prompt()
                                           .getContents();

        PromptTemplate promptTemplate = PromptTemplate.builder()
                                                      .template(DEFAULT_USER_TEXT_ADVISE)
                                                      .build();
        String preInputQuery = promptTemplate.render(Map.of("pre_input_query", contents));

        return chatClientRequest.mutate() // mutate复制
                                .prompt(Prompt.builder()
                                              .content(preInputQuery)
                                              .build())  // 写回重构后的提示词
                                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
