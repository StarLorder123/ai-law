import React, { useState } from 'react';
import { Input, Button } from 'antd';
import { SendOutlined, SyncOutlined } from '@ant-design/icons';
import { feedItems } from './mockData';
import AIChatBox from '../AIChatBox';
import './style.css';

const DynamicFeed = () => {
  const [activeTab, setActiveTab] = useState('1');
  
  // 移除原有的 feedItems 数据
  
  const renderContent = () => {
    switch(activeTab) {
      case '1':
        return (
          <div className="feed-list">
            {feedItems.map((item, index) => (
              <div key={index} className={`feed-item ${item.selected ? 'selected' : ''}`}>
                <div className="feed-time">
                  <div className="time-label">{item.type === 'today' ? '今天' : '昨天'}</div>
                  <div className="time">{item.time}</div>
                </div>
                <div className="feed-content">
                  {item.tag && <span className="feed-tag">{item.tag}</span>}
                  <div className="content-text">
                    {item.content.split(' ').map((part, i) => (
                      <span key={i} className={part.startsWith('微信公众号') ? 'highlight-text' : ''}>
                        {part}{' '}
                      </span>
                    ))}
                  </div>
                  {item.subContent && (
                    <div className="sub-content">
                      {item.subContent.map((text, i) => (
                        <div key={i}>{text}</div>
                      ))}
                    </div>
                  )}
                  {item.image && (
                    <div className="content-image">
                      <img src={item.image} alt="" />
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        );
      case '2':
        return <div>案件内容</div>;
      case '3':
        return <div>热点内容</div>;
      default:
        return null;
    }
  };

  return (
    <div className="dynamic-feed-container">
      <div className="dynamic-feed">
        <div className="feed-input">
          <Input 
            placeholder="想从AI那里得到什么？" 
            suffix={
              <>
                <Button type="text" icon={<SyncOutlined />} />
                <Button type="text" icon={<SendOutlined />} style={{ color: '#1890ff' }} />
              </>
            }
          />
        </div>
        
        <div className="custom-tabs-header">
          <div className="tab-list">
            <div 
              className={`tab-item ${activeTab === '1' ? 'active' : ''}`}
              onClick={() => setActiveTab('1')}
            >
              动态
            </div>
            <div 
              className={`tab-item ${activeTab === '2' ? 'active' : ''}`}
              onClick={() => setActiveTab('2')}
            >
              案件
            </div>
            <div 
              className={`tab-item ${activeTab === '3' ? 'active' : ''}`}
              onClick={() => setActiveTab('3')}
            >
              热点
            </div>
          </div>
          <Button type="link" className="add-button">+ 新增</Button>
        </div>
        
        <div className="tab-content">
          {renderContent()}
        </div>
      </div>
      <AIChatBox />
    </div>
  );
};

export default DynamicFeed;