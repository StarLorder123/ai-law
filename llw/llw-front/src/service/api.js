import { axiosRequest } from './request';

// 案件相关的api接口
export const caseApi = {
    // 查询所有案件
    queryAll: async (data) => axiosRequest.post('/query_base_case', data),

    queryChatList: async (data) => axiosRequest.post('/query_chat_list', data),

    queryChatContentList: async (data) => axiosRequest.post('/query_chat', data),

    // 流式请求接口
    streamMemory: async (bodyData, handlefn) => {
        try {
            const response = await fetch('/llw/v1/ai-stream-memory', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(bodyData)
            });

            const reader = response.body.getReader();
            const decoder = new TextDecoder();

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                // 解码收到的数据
                const text = decoder.decode(value);
                // console.log('收到数据:', text);
                handlefn(text);
                // 这里可以处理收到的数据，比如更新到状态中
            }
        } catch (error) {
            console.error('Stream request failed:', error);
            throw error;
        }
    },

    // 工具类chat接口
    toolMemory: async (bodyData, handlefn) => {
        try {
            const response = await fetch('/llw/v1/ai-tool-memory', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(bodyData)
            });

            const reader = response.body.getReader();
            const decoder = new TextDecoder();

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                // 解码收到的数据
                const text = decoder.decode(value);
                // console.log('收到数据:', text);
                handlefn(text);
                // 这里可以处理收到的数据，比如更新到状态中
            }
        } catch (error) {
            console.error('Stream request failed:', error);
            throw error;
        }
    },
};