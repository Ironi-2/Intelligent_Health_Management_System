package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.HealthAdviceResponse;
import org.example.model.HealthProfile;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HealthService {

    private final ChatClient chatClient;

    public HealthService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * 根据用户健康档案生成个性化健康建议
     */
    public HealthAdviceResponse generateHealthAdvice(HealthProfile profile) {
        // 计算BMI
        double heightInMeters = profile.getHeight() / 100.0;
        double bmi = profile.getWeight() / (heightInMeters * heightInMeters);
        bmi = Math.round(bmi * 10.0) / 10.0; // 保留一位小数

        String bmiCategory = getBmiCategory(bmi);

        // 构建AI提示词
        String prompt = buildPrompt(profile, bmi, bmiCategory);

        log.info("为用户 {} 生成健康建议, BMI: {}", profile.getName(), bmi);

        // 调用AI生成建议
        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        log.info("AI健康建议生成完成");

        // 解析AI响应(简单分割)
        HealthAdviceResponse response = parseAiResponse(aiResponse);
        response.setBmi(bmi);
        response.setBmiCategory(bmiCategory);

        return response;
    }

    /**
     * 根据BMI值返回分类
     */
    private String getBmiCategory(double bmi) {
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi < 24.9) {
            return "正常";
        } else if (bmi < 29.9) {
            return "超重";
        } else {
            return "肥胖";
        }
    }

    /**
     * 构建AI提示词
     */
    private String buildPrompt(HealthProfile profile, double bmi, String bmiCategory) {
        return String.format("""
                你是一位专业的健康管理师。请根据以下用户信息,提供个性化的健康建议和每日计划。
                
                【用户信息】
                - 姓名: %s
                - 年龄: %d岁
                - 性别: %s
                - 身高: %.1f cm
                - 体重: %.1f kg
                - BMI: %.1f (%s)
                - 饮食习惯: %s
                - 既往病史: %s
                - 运动习惯: %s
                - 健康目标: %s
                - 食物过敏: %s
                
                【要求】
                请严格按照以下JSON格式返回,不要有多余的文字:
                {
                  "healthAdvice": "个性化健康建议(200字以内)",
                  "dailyDietPlan": "每日饮食计划,包括早中晚餐和加餐建议(300字以内)",
                  "dailyExercisePlan": "每日运动计划,包括运动类型、时长和强度(200字以内)",
                  "lifestyleAdvice": "生活作息建议,包括睡眠、饮水、减压等(200字以内)"
                }
                
                注意事项:
                1. 建议要具体、可执行,考虑用户的既往病史和过敏情况
                2. 饮食计划要符合用户的饮食习惯和健康目标
                3. 运动计划要考虑用户的运动习惯和身体状况
                4. 避免推荐用户过敏的食物
                5. 用中文回答
                """,
                profile.getName(),
                profile.getAge(),
                profile.getGender(),
                profile.getHeight(),
                profile.getWeight(),
                bmi,
                bmiCategory,
                profile.getDietHabit(),
                profile.getMedicalHistory(),
                profile.getExerciseHabit(),
                profile.getHealthGoal(),
                profile.getAllergies() != null ? profile.getAllergies() : "无");
    }

    /**
     * 解析AI响应
     */
    private HealthAdviceResponse parseAiResponse(String aiResponse) {
        HealthAdviceResponse response = new HealthAdviceResponse();
        
        try {
            // 尝试提取JSON部分
            String jsonStr = aiResponse;
            int jsonStart = aiResponse.indexOf("{");
            int jsonEnd = aiResponse.lastIndexOf("}") + 1;
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                jsonStr = aiResponse.substring(jsonStart, jsonEnd);
            }
            
            // 简单解析JSON(生产环境建议使用Jackson)
            response.setHealthAdvice(extractJsonValue(jsonStr, "healthAdvice"));
            response.setDailyDietPlan(extractJsonValue(jsonStr, "dailyDietPlan"));
            response.setDailyExercisePlan(extractJsonValue(jsonStr, "dailyExercisePlan"));
            response.setLifestyleAdvice(extractJsonValue(jsonStr, "lifestyleAdvice"));
            
        } catch (Exception e) {
            log.error("解析AI响应失败: {}", e.getMessage(), e);
            response.setHealthAdvice("健康建议生成失败,请稍后重试");
            response.setDailyDietPlan("饮食计划生成失败");
            response.setDailyExercisePlan("运动计划生成失败");
            response.setLifestyleAdvice("生活建议生成失败");
        }
        
        return response;
    }

    /**
     * 从JSON字符串中提取值
     */
    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex < 0) {
                return "暂无建议";
            }
            
            int colonIndex = json.indexOf(":", keyIndex);
            int quoteStart = json.indexOf("\"", colonIndex) + 1;
            int quoteEnd = json.indexOf("\"", quoteStart);
            
            if (quoteStart > 0 && quoteEnd > quoteStart) {
                return json.substring(quoteStart, quoteEnd);
            }
        } catch (Exception e) {
            log.error("提取JSON值失败: {}", e.getMessage());
        }
        return "暂无建议";
    }
}
