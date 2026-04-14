package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ChatController {

    private final ChatClient chatClient;

    // Spring 会自动把配置好的 ChatClient.Builder 注入进来
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // 根路径重定向到 index.html
    @GetMapping("/")
    public void index(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.sendRedirect("/index.html");
    }

    // 定义一个访问路径为 /chat 的接口
    @GetMapping("/chat")
    public String chat(@RequestParam String question) {
        log.info("用户问：{}", question);

        // 核心调用逻辑：就这么简单的一行链式调用
        String response = chatClient.prompt()
                .user(question)
                .call()
                .content();

        log.info("AI答：{}", response);
        return response;
    }
}