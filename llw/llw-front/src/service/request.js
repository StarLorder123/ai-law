import axios from 'axios';
// import { message } from 'antd';

// 判断是否为开发环境
// const isDevelopment = process.env.NODE_ENV === 'development';

// 创建 axios 实例
export const axiosRequest = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/llw/v1', // 设置默认的请求基础URL
  timeout: 100000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
// request.interceptors.request.use(
//   (config) => {
//     // 在开发环境下跳过 token 验证
//     if (!isDevelopment) {
//       const token = localStorage.getItem('token');
//       if (token) {
//         config.headers.Authorization = `Bearer ${token}`;
//       }
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

// 响应拦截器
// request.interceptors.response.use(
//   (response) => {
//     const { data } = response;
//     // 在开发环境下直接返回数据
//     if (isDevelopment) {
//       return data;
//     }
    
//     // 生产环境下的正常验证逻辑
//     if (data.code === 200) {
//       return data.data;
//     }
//     message.error(data.message || '请求失败');
//     return Promise.reject(data);
//   },
//   (error) => {
//     // 在开发环境下简化错误处理
//     if (isDevelopment) {
//       message.error('请求失败');
//       return Promise.reject(error);
//     }

//     // 生产环境的完整错误处理
//     if (error.response) {
//       switch (error.response.status) {
//         case 401:
//           message.error('请重新登录');
//           break;
//         case 403:
//           message.error('没有权限访问');
//           break;
//         case 404:
//           message.error('请求的资源不存在');
//           break;
//         case 500:
//           message.error('服务器错误');
//           break;
//         default:
//           message.error('网络错误');
//       }
//     } else {
//       message.error('网络连接失败');
//     }
//     return Promise.reject(error);
//   }
// );

// 创建统一的 fetch 请求工具
export const fetchRequest = {
  // 基础 URL
  baseURL: '/llw/v1',

  // 默认请求头
  defaultHeaders: {
    'Content-Type': 'application/json',
  },

  // 普通请求
  async request(url, options = {}) {
    try {
      const finalUrl = `${this.baseURL}${url}`;
      const response = await fetch(finalUrl, {
        ...options,
        headers: {
          ...this.defaultHeaders,
          ...options.headers,
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data;
    } catch (error) {
      console.error('Fetch error:', error);
      throw error;
    }
  },

  // 流式请求
  async streamRequest(url, options = {}, onMessage) {
    try {
      const finalUrl = `${this.baseURL}${url}`;
      const response = await fetch(finalUrl, {
        ...options,
        headers: {
          ...this.defaultHeaders,
          ...options.headers,
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        // 解码收到的数据
        const text = decoder.decode(value);
        buffer += text;
        console.log(`text:${text}`)

        // 处理换行分隔的消息
        const messages = buffer.split('\n');
        buffer = messages.pop() || ''; // 保留最后一个可能不完整的消息

        // 处理完整的消息
        for (const message of messages) {
          if (message.trim()) {
            try {
              // 尝试解析 JSON
              const parsedMessage = JSON.parse(message);
              onMessage?.(parsedMessage);
            } catch (e) {
              // 如果不是 JSON，直接传递文本
              onMessage?.(message);
            }
          }
        }
      }

      // 处理最后的缓冲区
      if (buffer.trim()) {
        try {
          const parsedMessage = JSON.parse(buffer);
          onMessage?.(parsedMessage);
        } catch (e) {
          onMessage?.(buffer);
        }
      }
    } catch (error) {
      console.error('Stream request failed:', error);
      throw error;
    }
  },
};


