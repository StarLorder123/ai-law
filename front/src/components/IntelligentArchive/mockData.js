export const mockCases = [
  {
    id: '1',
    caseName: '张三合同纠纷案',
    caseNumber: 'HTJF2024001',
    createTime: '2024-01-15',
    status: '进行中',
    fileCount: 5,
    files: [
      {
        title: '案件材料',
        key: '1-0',
        children: [
          {
            title: '起诉文书',
            key: '1-0-0',
            children: [
              { title: '起诉状.docx', key: '1-0-0-0', isLeaf: true },
              { title: '委托书.pdf', key: '1-0-0-1', isLeaf: true },
            ],
          },
          {
            title: '证据材料',
            key: '1-0-1',
            children: [
              { title: '合同原件.pdf', key: '1-0-1-0', isLeaf: true },
              { title: '付款凭证.jpg', key: '1-0-1-1', isLeaf: true },
            ],
          },
        ],
      },
    ]
  },
  {
    id: '2',
    caseName: '李四知识产权案',
    caseNumber: 'ZSCQ2024002',
    createTime: '2024-01-14',
    status: '已结案',
    fileCount: 8,
    files: [
      {
        title: '案件材料',
        key: '2-0',
        children: [
          {
            title: '诉讼文书',
            key: '2-0-0',
            children: [
              { title: '起诉书.pdf', key: '2-0-0-0', isLeaf: true },
              { title: '授权委托.docx', key: '2-0-0-1', isLeaf: true },
            ],
          },
          {
            title: '证据文件',
            key: '2-0-1',
            children: [
              { title: '专利证书.pdf', key: '2-0-1-0', isLeaf: true },
              { title: '侵权证据.jpg', key: '2-0-1-1', isLeaf: true },
              { title: '对比文件.docx', key: '2-0-1-2', isLeaf: true },
            ],
          },
        ],
      },
    ]
  }
];