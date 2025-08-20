package com.danbobo.platformandmodels.controller;

import com.danbobo.platformandmodels.entity.MorePlatformAndModelOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Danbobo
 * @Description
 * @date 2025/8/20 14:00
 */
@RestController
@RequiredArgsConstructor
public class MorePlatformAndModel {

    Map<String, ChatModel> platformMap = new HashMap<>();

    public MorePlatformAndModel(DeepSeekChatModel deepSeekChatModel,
                                OllamaChatModel ollamaChatModel) {
        platformMap.put("deepseek", deepSeekChatModel);
        platformMap.put("ollama", ollamaChatModel);
    }


    @GetMapping("/chat")
    public Flux<String> chat(String message, MorePlatformAndModelOptions options) {
        String platform = options.getPlatform();
        ChatModel chatModel = platformMap.get(platform);

        ChatClient.Builder builder = ChatClient.builder(chatModel);

        ChatClient chatClient = builder.defaultOptions(ChatOptions.builder()
                        .temperature(options.getTemperature())
                        .model(options.getModel()).build())
                .build();

        Flux<String> content = chatClient.prompt().user(message).stream().content();
        return content;
    }
}
