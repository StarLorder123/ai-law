import React from 'react';
import { Card, Table, Button, Space, Tag } from 'antd';
import { EyeOutlined, EditOutlined } from '@ant-design/icons';

const CaseManagement = () => {
  const columns = [
    {
      title: '案件编号',
      dataIndex: 'caseNumber',
      key: 'caseNumber',
    },
    {
      title: '案件名称',
      dataIndex: 'caseName',
      key: 'caseName',
    },
    {
      title: '当事人',
      dataIndex: 'client',
      key: 'client',
    },
    {
      title: '案件类型',
      dataIndex: 'caseType',
      key: 'caseType',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={status === '进行中' ? 'processing' : 'success'}>
          {status}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" icon={<EyeOutlined />}>
            查看
          </Button>
          <Button type="link" icon={<EditOutlined />}>
            编辑
          </Button>
        </Space>
      ),
    },
  ];

  const data = [
    {
      key: '1',
      caseNumber: 'CASE2024001',
      caseName: '张三合同纠纷案',
      client: '张三',
      caseType: '合同纠纷',
      status: '进行中',
    },
    {
      key: '2',
      caseNumber: 'CASE2024002',
      caseName: '李四知识产权案',
      client: '李四',
      caseType: '知识产权',
      status: '已结案',
    },
  ];

  return (
    <Card title="案件管理" bordered={false}>
      <Table columns={columns} dataSource={data} />
    </Card>
  );
};

export default CaseManagement;