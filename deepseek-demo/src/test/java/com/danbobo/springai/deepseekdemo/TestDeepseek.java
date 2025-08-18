package com.danbobo.springai.deepseekdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @author Danbobo
 * @Description
 * @date 2025/8/18 14:51
 */
@SpringBootTest
@Slf4j
public class TestDeepseek {

    @Test
    public void testDeepseek(@Autowired DeepSeekChatModel deepSeekChatModel) {
        // call是阻塞式的
        String content = deepSeekChatModel.call("凤凰园胸针是谁？");
        System.out.println(content);
    }

    @Test
    public void testDeepseekStream(@Autowired DeepSeekChatModel deepSeekChatModel) {
        // 流式，非阻塞式输出
        Flux<String> stream = deepSeekChatModel.stream("命运石之门最可爱的是谁?");
        stream.toIterable().forEach(System.out::println);

    }


    @Test
    public void testDeepseekCustomByTemperature(@Autowired DeepSeekChatModel deepSeekChatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .temperature(0.9)  // 值越高，越发散，容易幻觉  0.5~0.8 日常建议值
//                .maxTokens()  // 根据字数截断 。(默认为32k ， 最大为64k 。)
//                .stop()  // 截断你不想输出的内容
                .build();
        Prompt prompt = new Prompt("如何评价msc攻略组在弧光作战旧约中成功登顶。",options);
        ChatResponse res = deepSeekChatModel.call(prompt);
        System.out.println(res.getResult().getOutput().getText());
    }

    @Test
    public void testDeepseekToHighWithR1(@Autowired DeepSeekChatModel deepSeekChatModel) {
        Prompt prompt = new Prompt("who are you?");
        ChatResponse chatResponse = deepSeekChatModel.call(prompt);

        // 输出内容转换为 DeepSeekAssistantMessage 类，  可以获取思考内容的获取方法值
        DeepSeekAssistantMessage deepSeekAssistantMessage = (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
        String reasoningContent = deepSeekAssistantMessage.getReasoningContent();
        String text = deepSeekAssistantMessage.getText();

        log.info("深度思考思维链内容{}",reasoningContent);
        log.info("++++++++++++++++++++++++++++++++++");
        log.info("输出内容{}",text);
    }

    @Test
    public void testDeepseekStreamToHighWithR1(@Autowired DeepSeekChatModel deepSeekChatModel) {
        Flux<ChatResponse> stream = deepSeekChatModel.stream(new Prompt("who are you?"));
        stream.toIterable().forEach(chatResponse -> {
            // 输出内容转换为 DeepSeekAssistantMessage 类，  可以获取思考内容的获取方法值
            DeepSeekAssistantMessage deepSeekAssistantMessage = (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();

            log.info("深度思考思维链内容{}",deepSeekAssistantMessage.getReasoningContent());
            log.info("输出内容{}",deepSeekAssistantMessage.getText());
        });


    }
}
