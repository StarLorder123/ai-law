import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
// import App from './App';
import 'antd/dist/reset.css';
// import YourComponent from './components/test';
// import DataList from './components/dataList';
import { ConfigProvider } from 'antd';
import { RouterElement } from './routes/routes';
import { BrowserRouter } from 'react-router-dom';
import zhCN from 'antd/locale/zh_CN';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ConfigProvider locale={zhCN}>
    {/* <App/> */}
    <BrowserRouter>
      <RouterElement />
    </BrowserRouter>
  </ConfigProvider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();
