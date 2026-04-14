// 全局状态
let currentChatId = Date.now();
let chatHistories = JSON.parse(localStorage.getItem('chatHistories') || '[]');
let isWaiting = false;

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    loadChatHistories();
    document.getElementById('messageInput').focus();
});

// 发送消息
async function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();
    
    if (!message || isWaiting) return;
    
    // 隐藏欢迎消息
    hideWelcomeMessage();
    
    // 添加用户消息
    addMessage(message, 'user');
    
    // 清空输入框
    input.value = '';
    input.style.height = 'auto';
    
    // 显示加载状态
    setLoading(true);
    
    try {
        // 调用后端 API
        const response = await fetch(`/chat?question=${encodeURIComponent(message)}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const answer = await response.text();
        
        // 添加 AI 回复
        addMessage(answer, 'ai');
        
        // 更新聊天历史
        updateChatHistory(message);
        
    } catch (error) {
        console.error('Error:', error);
        addMessage('抱歉,出现了错误。请稍后再试。', 'ai');
    } finally {
        setLoading(false);
    }
}

// 添加消息到聊天区域
function addMessage(text, type) {
    const container = document.getElementById('messagesContainer');
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    
    const icon = document.createElement('div');
    icon.className = 'message-icon';
    icon.textContent = type === 'user' ? '👤' : '🤖';
    
    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';
    bubble.textContent = text;
    
    messageDiv.appendChild(icon);
    messageDiv.appendChild(bubble);
    container.appendChild(messageDiv);
    
    // 滚动到底部
    container.scrollTop = container.scrollHeight;
}

// 显示正在输入指示器
function showTypingIndicator() {
    const container = document.getElementById('messagesContainer');
    
    const typingDiv = document.createElement('div');
    typingDiv.className = 'message ai';
    typingDiv.id = 'typingIndicator';
    
    const icon = document.createElement('div');
    icon.className = 'message-icon';
    icon.textContent = '🤖';
    
    const bubble = document.createElement('div');
    bubble.className = 'message-bubble typing-indicator';
    bubble.innerHTML = '<span></span><span></span><span></span>';
    
    typingDiv.appendChild(icon);
    typingDiv.appendChild(bubble);
    container.appendChild(typingDiv);
    
    container.scrollTop = container.scrollHeight;
}

// 移除正在输入指示器
function removeTypingIndicator() {
    const indicator = document.getElementById('typingIndicator');
    if (indicator) {
        indicator.remove();
    }
}

// 隐藏欢迎消息
function hideWelcomeMessage() {
    const welcome = document.querySelector('.welcome-message');
    if (welcome) {
        welcome.style.display = 'none';
    }
}

// 设置加载状态
function setLoading(loading) {
    isWaiting = loading;
    const sendBtn = document.getElementById('sendBtn');
    const sendIcon = document.getElementById('sendIcon');
    const loadingIcon = document.getElementById('loadingIcon');
    
    if (loading) {
        sendBtn.disabled = true;
        sendIcon.style.display = 'none';
        loadingIcon.style.display = 'inline';
        showTypingIndicator();
    } else {
        sendBtn.disabled = false;
        sendIcon.style.display = 'inline';
        loadingIcon.style.display = 'none';
        removeTypingIndicator();
    }
}

// 处理键盘事件
function handleKeyDown(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
}

// 自动调整文本框高度
function autoResizeTextarea(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px';
}

// 发送快速问题
function sendQuickQuestion(question) {
    document.getElementById('messageInput').value = question;
    sendMessage();
}

// 新建对话
function newChat() {
    currentChatId = Date.now();
    document.getElementById('currentChatTitle').textContent = '新对话';
    document.getElementById('messagesContainer').innerHTML = `
        <div class="welcome-message">
            <div class="welcome-icon">🤖</div>
            <h2>你好!我是 AI 智能助手</h2>
            <p>我可以帮助你解答问题、提供建议、完成任务等。请随时向我提问!</p>
            <div class="quick-actions">
                <button class="quick-action-btn" onclick="sendQuickQuestion('你能做什么?')">你能做什么?</button>
                <button class="quick-action-btn" onclick="sendQuickQuestion('帮我写一首诗')">帮我写一首诗</button>
                <button class="quick-action-btn" onclick="sendQuickQuestion('解释一下人工智能')">解释一下人工智能</button>
                <button class="quick-action-btn" onclick="sendQuickQuestion('给我讲个笑话')">给我讲个笑话</button>
            </div>
        </div>
    `;
    
    // 更新活跃状态
    document.querySelectorAll('.chat-history-item').forEach(item => {
        item.classList.remove('active');
    });
}

// 清空聊天
function clearChat() {
    if (confirm('确定要清空当前对话吗?')) {
        newChat();
    }
}

// 导出聊天
function exportChat() {
    const messages = document.querySelectorAll('.message');
    let exportText = 'AI 助手对话记录\n';
    exportText += '=' .repeat(50) + '\n\n';
    exportText += `时间: ${new Date().toLocaleString('zh-CN')}\n\n`;
    exportText += '-'.repeat(50) + '\n\n';
    
    messages.forEach(msg => {
        const isUser = msg.classList.contains('user');
        const bubble = msg.querySelector('.message-bubble');
        const type = isUser ? '用户' : 'AI';
        exportText += `${type}: ${bubble.textContent}\n\n`;
    });
    
    const blob = new Blob([exportText], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `chat-export-${currentChatId}.txt`;
    a.click();
    URL.revokeObjectURL(url);
}

// 更新聊天历史
function updateChatHistory(firstMessage) {
    // 查找是否已存在
    const existingIndex = chatHistories.findIndex(chat => chat.id === currentChatId);
    
    const chatData = {
        id: currentChatId,
        title: firstMessage.substring(0, 30) + (firstMessage.length > 30 ? '...' : ''),
        timestamp: Date.now(),
        firstMessage: firstMessage
    };
    
    if (existingIndex >= 0) {
        chatHistories[existingIndex] = chatData;
    } else {
        chatHistories.unshift(chatData);
    }
    
    // 只保留最近的 20 个对话
    if (chatHistories.length > 20) {
        chatHistories = chatHistories.slice(0, 20);
    }
    
    localStorage.setItem('chatHistories', JSON.stringify(chatHistories));
    loadChatHistories();
}

// 加载聊天历史
function loadChatHistories() {
    const container = document.getElementById('chatHistory');
    container.innerHTML = '';
    
    chatHistories.forEach(chat => {
        const item = document.createElement('div');
        item.className = 'chat-history-item';
        if (chat.id === currentChatId) {
            item.classList.add('active');
        }
        item.textContent = chat.title;
        item.onclick = () => loadChat(chat.id);
        container.appendChild(item);
    });
}

// 加载特定聊天
function loadChat(chatId) {
    // 这里可以实现加载特定聊天的功能
    // 目前只是更新活跃状态
    currentChatId = chatId;
    const chat = chatHistories.find(c => c.id === chatId);
    if (chat) {
        document.getElementById('currentChatTitle').textContent = chat.title;
    }
    
    document.querySelectorAll('.chat-history-item').forEach(item => {
        item.classList.remove('active');
    });
    event.target.classList.add('active');
}
