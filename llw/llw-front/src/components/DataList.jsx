import React, { useState, useEffect } from 'react';
import { Typography, Flex, Card, Input } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { caseApi } from '../service/api';
import { useNavigate } from 'react-router-dom';

const { Paragraph, Text } = Typography;

const DataList = () => {

  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [dataSource, setDataSource] = useState([
    {
      id: 1, title: '标题1', description: `Ant Design, a design language for background applications, is refined by Ant UED Team. Ant
      Design, a design language for background applications, is refined by Ant UED Team. Ant
      Design, a design language for background applications, is refined by Ant UED Team. Ant
      Design, a design language for background applications, is refined by Ant UED Team. Ant
      Design, a design language for background applications, is refined by Ant UED Team. Ant
      Design, a design language for background applications, is refined by Ant UED Team.`, status: '进行中'
    },
    { id: 2, title: '标题2', description: '描述2', status: '已完成' },
    { id: 3, title: '标题3', description: '描述3', status: '待处理' },
  ]);

  useEffect(() => {
    caseApi.queryAll(JSON.stringify({})).then(data => {
      const caseData = []
      data.data.data.caseList.map(item => {
        caseData.push({
          id: item.id,
          description: item.content,
          status: 'ceshi'
        })
      })
      setDataSource(caseData)
      console.log(`data:${JSON.stringify(data)}`)
    })

  }, [])

  return (
    <div style={{ padding: '24px' }}>
      <Flex justify={'center'} align={'center'} style={{ width: '100%' }}>
        <div style={{ width: '60%' }}>
          <Input size="large" placeholder="large size" prefix={<SearchOutlined />} style={{ marginBottom: '25px' }} />
          {
            dataSource.map(item => {
              return (
                <Card onClick={() => { navigate(`/case/${item.id}`) }} style={{ width: '100%' }}>
                  <Paragraph ellipsis={{ rows: 3 }}>
                    {item.description}
                  </Paragraph>
                </Card>
              )
            })
          }
        </div>
      </Flex>
    </div>
  );
};

export default DataList;