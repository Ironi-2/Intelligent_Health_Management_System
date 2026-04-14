package org.example.model;

import lombok.Data;

/**
 * 用户健康档案
 */
@Data
public class HealthProfile {
    
    /**
     * 用户姓名
     */
    private String name;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 性别: MALE/FEMALE/OTHER
     */
    private String gender;
    
    /**
     * 身高(cm)
     */
    private Double height;
    
    /**
     * 体重(kg)
     */
    private Double weight;
    
    /**
     * 饮食习惯: 如"素食"、"荤素搭配"、"低碳水"、"高蛋白"等
     */
    private String dietHabit;
    
    /**
     * 既往病史: 如"高血压"、"糖尿病"、"无"等,多个用逗号分隔
     */
    private String medicalHistory;
    
    /**
     * 运动习惯: 如"久坐"、"轻度运动"、"中度运动"、"高强度运动"
     */
    private String exerciseHabit;
    
    /**
     * 健康目标: 如"减重"、"增肌"、"保持健康"、"改善睡眠"等
     */
    private String healthGoal;
    
    /**
     * 食物过敏史
     */
    private String allergies;
}
