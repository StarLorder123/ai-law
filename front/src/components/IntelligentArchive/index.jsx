import React, { useState } from 'react';
import { Layout, Card, Button, Upload, Row, Col } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import CaseList from './CaseList';
import FilePreview from './FilePreview';
import FileTree from './FileTree';
import { mockCases } from './mockData';
import './style.css';

const { Content } = Layout;

const IntelligentArchive = () => {
  const [loading, setLoading] = useState(false);
  const [selectedCase, setSelectedCase] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);

  // 处理案件选择
  const handleCaseSelect = (caseData) => {
    setSelectedCase(caseData);
    setSelectedFile(null); // 清空已选文件
  };

  // 处理文件选择
  const onSelectFile = (selectedKeys, info) => {
    if (info.node.isLeaf) {
      setSelectedFile(info.node.title);
    }
  };

  return (
    <Layout className="intelligent-archive">
      <Content className="archive-content">
        <Card className="archive-card">
          {!selectedCase ? (
            <CaseList
              cases={mockCases}
              loading={loading}
              onSelectCase={handleCaseSelect}
            />
          ) : (
            <div className="case-view">
              <div className="case-header">
                <Button type="link" onClick={() => handleCaseSelect(null)}>
                  返回案件列表
                </Button>
                <h3>{selectedCase.caseName}</h3>
                <Upload>
                  <Button icon={<UploadOutlined />} type="primary">
                    上传文件
                  </Button>
                </Upload>
              </div>
              <Row>
                <Col span={18} className="preview-panel">
                  <FilePreview file={selectedFile} />
                </Col>
                <Col span={6} className="tree-panel">
                  <FileTree
                    treeData={selectedCase.files}
                    onSelect={onSelectFile}
                  />
                </Col>
              </Row>
            </div>
          )}
        </Card>
      </Content>
    </Layout>
  );
};

export default IntelligentArchive;