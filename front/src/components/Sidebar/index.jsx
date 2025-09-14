import React from 'react';
import { Avatar, Typography } from 'antd';
import { Link, useLocation } from 'react-router-dom';
import { 
  HomeOutlined, 
  SearchOutlined, 
  FolderOpenOutlined, 
  ApartmentOutlined,
  SettingOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';
import './style.css';

const { Text, Title } = Typography;

const Sidebar = () => {
  const location = useLocation();
  
  const getSelectedKey = () => {
    const path = location.pathname;
    if (path === '/') return '1';
    if (path === '/search') return '2';
    if (path === '/archive') return '3';
    if (path === '/workflow') return '4';
    if (path === '/settings') return '5';
    return '1';
  };

  const myCases = [
    { id: '1', name: '张某人身损害赔偿案' },
    { id: '2', name: '某科技公司股权纠纷案', selected: true },
    { id: '3', name: '王某婚姻家庭纠纷案' },
    { id: '4', name: '刘某劳动合同纠纷案' },
    { id: '5', name: '赵某知识产权案' },
  ];

  const menuItems = [
    { key: '1', icon: <HomeOutlined />, label: '首页', path: '/' },
    { key: '2', icon: <SearchOutlined />, label: '法道检索', path: '/search' },
    { key: '3', icon: <FolderOpenOutlined />, label: '智能案件', path: '/archive' },
    { key: '4', icon: <ApartmentOutlined />, label: '工作流程', path: '/workflow' },
    { key: '5', icon: <SettingOutlined />, label: '设置', path: '/settings' },
  ];

  const selectedKey = getSelectedKey();

  return (
    <div className="sidebar">
      <div className="user-profile">
        <Avatar size={48} style={{ backgroundColor: '#1890ff' }}>李</Avatar>
        <div className="user-info">
          <Title level={5} className="user-name">李四 律师，您好</Title>
          <Text type="secondary" className="user-firm">锦天城律师事务所</Text>
          <div className="user-privacy">大公无私，大道至简</div>
        </div>
      </div>
      
      <nav className="nav-menu">
        {menuItems.map(item => (
          <Link 
            key={item.key}
            to={item.path}
            className={`nav-item ${selectedKey === item.key ? 'active' : ''}`}
          >
            <span className="nav-icon">{item.icon}</span>
            <span className="nav-label">{item.label}</span>
          </Link>
        ))}
      </nav>
      
      <div className="my-cases">
        <div className="cases-header">
          <ClockCircleOutlined /> 我的案件
        </div>
        <div className="cases-list">
          {myCases.map(caseItem => (
            <Link 
              key={caseItem.id}
              to={`/cases/${caseItem.id}`}
              className={`case-item ${caseItem.selected ? 'selected' : ''}`}
            >
              {caseItem.name}
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Sidebar;