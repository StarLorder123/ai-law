import React from 'react';
import { Card, Image } from 'antd';
import { Document, Page } from 'react-pdf';
import DocViewer, { DocViewerRenderers } from "@cyntler/react-doc-viewer";
import './FilePreview.css';

const FilePreview = ({ file }) => {
  if (!file) {
    return (
      <Card title="文件预览" bordered={false} className="preview-card">
        <div className="empty-preview">
          请从右侧选择要预览的文件
        </div>
      </Card>
    );
  }

  // 获取文件扩展名
  const fileExtension = file.toLowerCase().split('.').pop();

  // 根据文件类型渲染不同的预览组件
  const renderPreview = () => {
    switch (fileExtension) {
      case 'pdf':
        return (
          <Document
            file={`/api/files/${file}`}
            className="pdf-container"
          >
            <Page pageNumber={1} />
          </Document>
        );
      
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif':
        return (
          <Image
            src={`/api/files/${file}`}
            alt={file}
            className="image-preview"
          />
        );
      
      case 'doc':
      case 'docx':
      case 'xlsx':
      case 'pptx':
        return (
          <DocViewer
            documents={[{ uri: `/api/files/${file}` }]}
            pluginRenderers={DocViewerRenderers}
            className="doc-viewer"
          />
        );
      
      default:
        return (
          <div className="unsupported-preview">
            暂不支持该文件格式的预览
          </div>
        );
    }
  };

  return (
    <Card title="文件预览" bordered={false} className="preview-card">
      <div className="file-preview">
        {renderPreview()}
      </div>
    </Card>
  );
};

export default FilePreview;