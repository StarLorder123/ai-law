import React from 'react';
import { Table, Space, Button, Input, Tag, Typography } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import './CaseList.css';

const { Search } = Input;
const { Text } = Typography;

const CaseList = ({ cases, loading, onSelectCase }) => {
  // 状态对应的颜色
  const statusColors = {
    '进行中': 'processing',
    '已结案': 'success',
    '待处理': 'warning',
    '终止': 'error'
  };

  const caseColumns = [
    {
      title: '案件信息',
      dataIndex: 'caseName',
      key: 'caseName',
      render: (_, record) => (
        <div className="case-info">
          <div className="case-title">
            <span className="case-name">{record.caseName}</span>
            <Text type="secondary" className="case-number">#{record.caseNumber}</Text>
            <Tag color={statusColors[record.status]} className="case-status">
              {record.status}
            </Tag>
          </div>
          <div className="case-summary">
            <Text type="secondary">
              创建时间：{record.createTime} | 文件数量：{record.fileCount}
            </Text>
          </div>
        </div>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_, record) => (
        <Button type="primary" onClick={() => onSelectCase(record)}>
          查看文件
        </Button>
      ),
    },
  ];

  return (
    <>
      <div className="archive-header">
        <Space size="large">
          <Search
            placeholder="搜索案件"
            allowClear
            enterButton={<SearchOutlined />}
            size="large"
            style={{ width: 300 }}
          />
        </Space>
      </div>
      <Table
        columns={caseColumns}
        dataSource={cases}
        loading={loading}
        rowKey="id"
        pagination={{
          total: cases.length,
          pageSize: 10,
          showTotal: (total) => `共 ${total} 个案件`,
        }}
      />
    </>
  );
};

export default CaseList;