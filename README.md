# 智能健康管理系统

基于 Spring Boot 和 Spring AI 打造的个性化健康管理系统,根据用户的身体数据和健康状况,AI智能生成个性化建议和每日计划。

## 🌟 功能特性

- ✅ **健康档案管理**: 记录用户的身高、体重、饮食习惯、既往病史等
- ✅ **BMI智能计算**: 自动计算BMI并提供分类
- ✅ **AI健康建议**: 基于用户信息生成个性化健康建议
- ✅ **每日饮食计划**: 根据健康目标和饮食习惯定制
- ✅ **每日运动计划**: 根据身体状况和运动习惯制定
- ✅ **生活作息建议**: 提供睡眠、饮水、减压等全方位建议

## 🛠️ 技术栈

- **后端**: Spring Boot 3.4.2 + Spring AI 1.0.0
- **AI模型**: Ollama (qwen2.5:1.5b)
- **前端**: 原生HTML + CSS + JavaScript
- **构建工具**: Maven

## 📋 前置要求

1. Java 17 或更高版本
2. Maven 3.6+ 
3. Ollama 已安装并运行在本地 (端口11434)
4. 已下载 qwen2.5:1.5b 模型

## 🚀 快速开始

### 1. 启动 Ollama

确保 Ollama 服务正在运行:
```bash
ollama serve
```

### 2. 下载模型(如果还没有)
```bash
ollama pull qwen2.5:1.5b
```

### 3. 编译项目
```bash
mvn clean package -DskipTests
```

### 4. 运行应用
```bash
mvn spring-boot:run
```

或者直接运行打包后的jar:
```bash
java -jar target/ai-java-demo-0.0.1-SNAPSHOT.jar
```

### 5. 访问应用

打开浏览器访问: http://localhost:8080/health.html

## 📝 使用说明

1. 填写健康档案表单:
   - 基本信息: 姓名、年龄、性别
   - 身体数据: 身高、体重
   - 生活习惯: 饮食习惯、运动习惯
   - 健康状况: 既往病史、食物过敏
   - 健康目标: 减重/增肌/保持健康等

2. 点击"生成个性化健康建议"

3. AI会生成:
   - BMI分析
   - 个性化健康建议
   - 每日饮食计划
   - 每日运动计划
   - 生活作息建议

## 📁 项目结构

```
src/main/java/org/example/
├── AiJavaDemoApplication.java          # 主应用启动类
├── controller/
│   └── HealthController.java           # 健康建议REST接口
├── service/
│   └── HealthService.java              # AI健康建议生成服务
├── model/
│   └── HealthProfile.java              # 用户健康档案实体
└── dto/
    └── HealthAdviceResponse.java       # AI建议响应DTO

src/main/resources/
├── application.yml                     # 应用配置
└── static/
    └── health.html                     # 前端页面
```

## 🔌 API接口

### 生成健康建议
- **URL**: POST `/api/health/advice`
- **请求体**: HealthProfile JSON对象
- **响应**: HealthAdviceResponse JSON对象

示例请求:
```json
{
  "name": "张三",
  "age": 30,
  "gender": "男",
  "height": 175.0,
  "weight": 70.0,
  "dietHabit": "荤素搭配",
  "exerciseHabit": "中度运动",
  "healthGoal": "保持健康",
  "medicalHistory": "无",
  "allergies": "海鲜"
}
```

## ⚙️ 配置说明

在 `application.yml` 中:

```yaml
spring:
  ai:
    openai:
      api-key: ollama
      base-url: http://localhost:11434
      chat:
        options:
          model: qwen2.5:1.5b  # 可以改成其他模型
```

如果使用其他AI服务(如OpenAI、硅基流动等),修改对应配置即可。

## 🎨 界面预览

系统提供一个美观的单页面应用,包含:
- 渐变色背景
- 响应式表单设计
- 加载动画
- 结果展示卡片
- BMI可视化显示

## 📝 注意事项

1. 首次调用AI接口可能较慢,请耐心等待
2. 确保Ollama服务正常运行且模型已下载
3. 建议使用 qwen2.5 或更强大的模型以获得更好的建议质量
4. AI建议仅供参考,不作为医疗诊断依据

## 🔧 常见问题

**Q: 如何切换AI模型?**
A: 修改 `application.yml` 中的 `spring.ai.openai.chat.options.model` 配置

**Q: 支持其他AI服务商吗?**
A: 支持,修改 `base-url` 和 `api-key` 为对应服务商的地址和密钥

**Q: AI响应解析失败怎么办?**
A: 检查模型是否正确返回JSON格式,可以尝试更换模型

## 📄 许可证

MIT License

---

**健康提示**: 本系统AI建议仅供参考,如有健康问题请咨询专业医生。
