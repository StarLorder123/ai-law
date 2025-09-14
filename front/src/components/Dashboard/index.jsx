import React from 'react';
import { Card, Row, Col } from 'antd';

const Dashboard = () => {
  return (
    <div className="dashboard">
      <Row gutter={[16, 16]}>
        <Col span={6}>
          <Card title="待办事项" bordered={false}>
            暂无待办事项
          </Card>
        </Col>
        <Col span={6}>
          <Card title="最近案件" bordered={false}>
            暂无最近案件
          </Card>
        </Col>
        <Col span={6}>
          <Card title="日程提醒" bordered={false}>
            暂无日程提醒
          </Card>
        </Col>
        <Col span={6}>
          <Card title="统计数据" bordered={false}>
            暂无统计数据
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;