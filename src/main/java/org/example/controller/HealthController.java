package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.HealthAdviceResponse;
import org.example.model.HealthProfile;
import org.example.service.HealthService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * 生成个性化健康建议
     */
    @PostMapping("/advice")
    public HealthAdviceResponse generateAdvice(@RequestBody HealthProfile profile) {
        log.info("收到健康建议请求: {}", profile.getName());
        return healthService.generateHealthAdvice(profile);
    }

    /**
     * 健康页面
     */
    @GetMapping("/")
    public void healthPage(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.sendRedirect("/health.html");
    }
}
