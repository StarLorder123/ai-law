import { useEffect } from 'react';
import { caseApi } from '../service/api';

const YourComponent = () => {
    const handleLogin = async () => {
        try {
            const data = await caseApi.queryAll();
            console.log('登录成功', data);
        } catch (error) {
            console.error('登录失败', error);
        }
    };

    useEffect(() => {
        handleLogin()
    }, [])

    return (
        // 你的组件内容
        <div>这是测试</div>
    );
};

export default YourComponent;