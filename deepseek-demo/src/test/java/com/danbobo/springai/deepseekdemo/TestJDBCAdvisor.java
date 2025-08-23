package com.danbobo.springai.deepseekdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Danbobo
 * @Description
 * @date 2025/8/23 14:53
 */
@SpringBootTest
@Slf4j
public class TestJDBCAdvisor {

    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired DeepSeekChatModel deepSeekChatModel,
                     @Autowired ChatMemory chatMemory) {
        ChatClient.builder(deepSeekChatModel)
                  .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory)
                                                          .build())
                  .build();
    }

    @TestConfiguration
    static class config {
        @Bean
        ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory.builder()
                                          .maxMessages(11)
                                          .chatMemoryRepository(chatMemoryRepository)
                                          .build();
        }

    }


    @Test
    public void testChatOptions() {
        String content = chatClient.prompt()
                                   .user("我是牧濑红莉栖，一名天才科学家少女。")
                                   .call()
                                   .content();
        log.info(content);

        String content1 = chatClient.prompt()
                                    .user("我是谁")
                                    .call()
                                    .content();
        log.info(content1);
    }

}
