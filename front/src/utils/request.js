import axios from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: 'http://localhost:3000/api',
  timeout: 10000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等认证信息
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    message.error(error.response?.data?.message || '请求失败');
    return Promise.reject(error);
  }
);

export default request;