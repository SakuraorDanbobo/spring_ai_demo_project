package com.danbobo.springai.deepseekdemo;

import com.danbobo.springai.deepseekdemo.Advisor.MyChatReadingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Danbobo
 * @Description
 * @date 2025/8/20 14:55
 */
@SpringBootTest
@Slf4j
public class TestAdvisor {

    // 内置简易日志拦截器测试
    @Test
    public void testSimpleLoggerAdvisor(@Autowired ChatClient.Builder chatClientBuilder) {
        /**
         * 1. 使用内置简易日志拦截器SimpleLoggerAdvisor 。 logging.org.springframework.ai.chat.client.advisor = debug
         * 2. yml配置文件中开启对应的日志级别
         */
        ChatClient chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor())
                                                 .build();

        String content = chatClient.prompt()
                                   .user("如何评价msc攻略组在弧光作战旧约中成功登顶")
                                   .call()
                                   .content();
        log.info(content);

    }

    // 内置敏感词拦截器测试
    @Test
    public void SafeGuardAdvisor(@Autowired ChatClient.Builder chatClientBuilder) {
        /**
         * 检测在传入大模型前，直接截断，检查用户提示词。
         */
        ChatClient chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor(),
                                                         new SafeGuardAdvisor(List.of("共产党", "中考", "高考"),
                                                                 "存在敏感内容，请勿多次尝试",0))
                                                 .build();
        String content = chatClient.prompt()
                                   .user("中国共产党发展史")
                                   .call()
                                   .content();
        System.out.println(content);
    }

    // 自定义拦截器测试
    @Test
    public void MyReplaceAdvisor(@Autowired ChatClient.Builder chatClientBuilder) {
        /**
         * 检测在传入大模型前，直接截断，检查用户提示词。
         */
        ChatClient chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor(),
                                                         new MyChatReadingAdvisor())
                                                 .build();
        String content = chatClient.prompt()
                                   .user("命运石之门的真由理有多喜欢红莉栖")
                                   .call()
                                   .content();
        System.out.println(content);
    }
}
