import React, { useState } from 'react';
import { Card, Tree, message } from 'antd';
import { FolderOutlined, FileOutlined } from '@ant-design/icons';
import './FileTree.css';

/**
 * 文件树组件
 * @param {Object[]} treeData - 初始树形数据
 * @param {Function} onSelect - 选择节点时的回调函数
 */
const FileTree = ({ treeData: initialTreeData, onSelect }) => {
  // 使用状态管理树形数据，支持拖拽更新
  const [treeData, setTreeData] = useState(initialTreeData);

  /**
   * 处理节点拖拽
   * @param {Object} info - 拖拽事件信息
   * info.node: 目标节点
   * info.dragNode: 被拖拽的节点
   * info.dropPosition: 放置位置
   * info.dropToGap: 是否放置在节点之间
   */
  const onDrop = (info) => {
    const dropKey = info.node.key;
    const dragKey = info.dragNode.key;
    const dropPos = info.node.pos.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);

    /**
     * 递归遍历树数据
     * @param {Object[]} data - 树数据
     * @param {string} key - 要查找的节点key
     * @param {Function} callback - 找到节点时的回调
     */
    const loop = (data, key, callback) => {
      for (let i = 0; i < data.length; i++) {
        if (data[i].key === key) {
          return callback(data[i], i, data);
        }
        if (data[i].children) {
          loop(data[i].children, key, callback);
        }
      }
    };

    // 复制树数据
    const data = [...treeData];
    let dragObj;

    // 找到并移除被拖拽的节点
    loop(data, dragKey, (item, index, arr) => {
      arr.splice(index, 1);
      dragObj = item;
    });

    // 处理放置逻辑
    if (!info.dropToGap) {
      // 放置到节点上，作为其子节点
      loop(data, dropKey, (item) => {
        item.children = item.children || [];
        item.children.unshift(dragObj);
      });
    } else if (
      (info.node.children || []).length > 0 && 
      info.node.expanded && 
      dropPosition === 1
    ) {
      // 放置到展开的文件夹的首位
      loop(data, dropKey, (item) => {
        item.children = item.children || [];
        item.children.unshift(dragObj);
      });
    } else {
      // 放置到节点前后
      let ar;
      let i;
      loop(data, dropKey, (item, index, arr) => {
        ar = arr;
        i = index;
      });
      if (dropPosition === -1) {
        ar.splice(i, 0, dragObj); // 放置到节点前
      } else {
        ar.splice(i + 1, 0, dragObj); // 放置到节点后
      }
    }

    // 更新树数据
    setTreeData(data);
    message.success('文件结构已更新');
  };

  return (
    <Card title="案件卷宗" bordered={false} className="tree-card">
      <Tree
        className="draggable-tree"
        showLine
        showIcon
        defaultExpandAll
        draggable
        blockNode
        onDrop={onDrop}
        treeData={treeData}
        onSelect={onSelect}
        icon={({ isLeaf }) => isLeaf ? <FileOutlined /> : <FolderOutlined />}
      />
    </Card>
  );
};

export default FileTree;