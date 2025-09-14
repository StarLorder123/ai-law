import React, { useState } from 'react';
import './style.css';

const AIChatBox = () => {
  const [messages, setMessages] = useState([
    { type: 'ai', content: '你好，今天天气不错!' },
    { type: 'user', content: '是啊，天气很好，出去走走?' },
    { type: 'user', content: '好啊，去哪里?' },
    { type: 'ai', content: '嗯嗯君子兰怎么样?' }
  ]);

  return (
    <div className="chat-container">
      <div className="chat-box">
        <div className="chat-messages">
          {messages.map((msg, index) => (
            <div key={index} className={`message ${msg.type}`}>
              <div className="message-content">{msg.content}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AIChatBox;