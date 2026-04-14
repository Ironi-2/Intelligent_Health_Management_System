package org.example.dto;

import lombok.Data;

/**
 * AI健康建议响应
 */
@Data
public class HealthAdviceResponse {
    
    /**
     * BMI指数
     */
    private Double bmi;
    
    /**
     * BMI分类
     */
    private String bmiCategory;
    
    /**
     * 个性化健康建议
     */
    private String healthAdvice;
    
    /**
     * 每日饮食计划
     */
    private String dailyDietPlan;
    
    /**
     * 每日运动计划
     */
    private String dailyExercisePlan;
    
    /**
     * 生活作息建议
     */
    private String lifestyleAdvice;
}
