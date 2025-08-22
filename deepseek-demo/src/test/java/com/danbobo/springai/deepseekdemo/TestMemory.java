package com.danbobo.springai.deepseekdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Danbobo
 * @Description
 * @date 2025/8/22 13:22
 */
@SpringBootTest
@Slf4j
public class TestMemory {

    private ChatClient chatClient;

    // 做初始化内容
    @BeforeEach
    public void init(@Autowired ChatClient.Builder builder,
                     @Autowired ChatMemory chatMemory) {
        chatClient = builder
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory)
                                                        .build())
                .build();
    }

    /**
     * 通过多轮对话实现记忆功能，  demo01 - 手动存储
     *
     * @param deepSeekChatModel
     */
    @Test
    public void testMemory01(@Autowired DeepSeekChatModel deepSeekChatModel) {
        // 记忆池
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                                                                    .build();
        String memoryId = "name01";
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel)
                                          .build();

        // 第一次对话
        UserMessage userMessage = new UserMessage("我是牧濑红莉栖，一名天才科学家少女。");
        chatMemory.add(memoryId, userMessage);
        ChatResponse resp1 = chatClient.prompt()
//                                .user() // 单次用户提问，不包含任何历史记录
                                       .messages(chatMemory.get(memoryId)) // 用于多轮对话，它接收一个包含所有历史消息的列表，确保 AI 能够维持对话的上下文
                                       .call()
                                       .chatResponse();
        chatMemory.add(memoryId, resp1.getResult()
                                      .getOutput());


        // 第二次对话
        UserMessage userMessage1 = new UserMessage("我叫什么？");
        chatMemory.add(memoryId, userMessage1);
        ChatResponse resp2 = chatClient.prompt()
                                       .messages(chatMemory.get(memoryId))
                                       .call()
                                       .chatResponse();
        chatMemory.add(memoryId, resp2.getResult()
                                      .getOutput());
        log.info(resp2.getResult()
                      .getOutput()
                      .getText());
    }


    /**
     * 通过多轮对话实现记忆功能，  demo02 - 拦截器存储
     *
     * @param deepSeekChatModel
     */
    @Test
    public void testMemory02(@Autowired DeepSeekChatModel deepSeekChatModel,
                             @Autowired ChatMemory chatMemory) {
        // 通过已实现的轮子类PromptChatMemoryAdvisor ，  来进行使用，根据业务场景需要可适当重构
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel)
                                          .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory)
                                                                                  .build())
                                          .build();
        // 第一次对话
        ChatResponse resp1 = chatClient.prompt()
                                       .user("我是牧濑红莉栖，一名天才科学家少女。") // 单次用户提问，不包含任何历史记录
                                       .call()
                                       .chatResponse();

        // 第二次对话
        ChatResponse resp2 = chatClient.prompt()
                                       .user("我叫什么？")
                                       .call()
                                       .chatResponse();
        log.info(resp2.getResult()
                      .getOutput()
                      .getText());
    }

    /**
     * 通过多轮对话实现记忆功能  demo03 —— 多用户隔离
     *
     * @param deepSeekChatModel
     */
    @Test
    public void testMemory03(@Autowired DeepSeekChatModel deepSeekChatModel,
                             @Autowired ChatMemory chatMemory) {

        // 用户1：第一次对话
        String resp1 = chatClient.prompt()
                                       .user("我是牧濑红莉栖，一名天才科学家少女。") // 单次用户提问，不包含任何历史记录
                                       .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "user01")) // 分组id设置，内容区分
                                       .call()
                                       .content();
        log.info(resp1);
        // 用户1：第二次对话
        String resp2 = chatClient.prompt()
                                       .user("我叫什么？")
                                       .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "user01"))
                                       .call()
                                       .content();
        log.info(resp2);

        // 用户2：第一次对话
        String resp3 = chatClient.prompt()
                                       .user("我叫什么？")
                                       .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "user02"))
                                       .call()
                                       .content();
        log.info(resp3);
    }


    @TestConfiguration
    public static class ChatMemoryConfig {
        @Bean
        ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
            return MessageWindowChatMemory.builder()
                                          .maxMessages(10)  // 记录最新条数,栈先进先出方式更新内容
                                          .chatMemoryRepository(chatMemoryRepository)
                                          .build();
        }
    }
}
