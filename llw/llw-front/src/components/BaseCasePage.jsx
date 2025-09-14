import {
  Attachments,
  Bubble,
  Conversations,
  Prompts,
  Sender,
  Welcome,
  useXAgent,
  useXChat,
} from '@ant-design/x';
import { createStyles } from 'antd-style';
import React, { useEffect, useState } from 'react';
import {
  CloudUploadOutlined,
  CommentOutlined,
  EllipsisOutlined,
  FireOutlined,
  HeartOutlined,
  PaperClipOutlined,
  PlusOutlined,
  ReadOutlined,
  ShareAltOutlined,
  SmileOutlined,
  UserOutlined,
  OpenAIOutlined,
  ToolOutlined,
  ArrowUpOutlined
} from '@ant-design/icons';
import { Badge, Button, Space, Typography, Spin, } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { caseApi } from '../service/api';
import markdownit from 'markdown-it';
import { generateRandom32BitNumber } from '../utils/random';
import './BaseCasePage.css';
import LOGO from '../resources/logo.svg';

const md = markdownit({
  html: true,
  breaks: true,
});

const renderTitle = (icon, title) => (
  <Space align="start">
    {icon}
    <span>{title}</span>
  </Space>
);
const defaultConversationsItems = [];
const useStyle = createStyles(({ token, css }) => {
  return {
    layout: css`
      width: 100%;
      min-width: 1000px;
      height: 100%;
      border-radius: ${token.borderRadius}px;
      display: flex;
      background: ${token.colorBgContainer};
      font-family: AlibabaPuHuiTi, ${token.fontFamily}, sans-serif;

      .ant-prompts {
        color: ${token.colorText};
      }
    `,
    menu: css`
      background: ${token.colorBgLayout}80;
      width: 15%;
      height: 100%;
      display: flex;
      flex-direction: column;
    `,
    conversations: css`
      padding: 0 12px;
      flex: 1;
      overflow-y: auto;
    `,
    chat: css`
      height: 100%;
      width: 50%;
      margin: 0 auto;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      padding: ${token.paddingLG}px;
      gap: 16px;
    `,
    messages: css`
      padding-left:20px;
      padding-right:20px;
      flex: 1;
    `,
    placeholder: css`
      padding-top: 32px;
    `,
    sender: css`
      box-shadow: ${token.boxShadow};
    `,
    logo: css`
      display: flex;
      height: 72px;
      align-items: center;
      justify-content: start;
      padding: 0 24px;
      box-sizing: border-box;

      img {
        width: 24px;
        height: 24px;
        display: inline-block;
      }

      span {
        display: inline-block;
        margin: 0 8px;
        font-weight: bold;
        color: ${token.colorText};
        font-size: 16px;
      }
    `,
    addBtn: css`
      background: #1677ff0f;
      border: 1px solid #1677ff34;
      width: calc(100% - 24px);
      margin: 0 12px 24px 12px;
    `,
  };
});
const placeholderPromptsItems = [
  {
    key: '1',
    label: renderTitle(
      <FireOutlined
        style={{
          color: '#FF4D4F',
        }}
      />,
      '热门操作',
    ),
    description: '哪个你感兴趣？',
    children: [
      {
        key: '1-1',
        description: `这篇文书什么内容？`,
      },
      {
        key: '1-2',
        description: `有哪些当事人？`,
      },
      {
        key: '1-3',
        description: `判决结果是什么？`,
      },
    ],
  },
  {
    key: '2',
    label: renderTitle(
      <ReadOutlined
        style={{
          color: '#1890FF',
        }}
      />,
      '使用指南',
    ),
    description: '指导你使用。',
    children: [
      {
        key: '2-1',
        icon: <HeartOutlined />,
        description: `了解智算大脑`,
      },
      {
        key: '2-2',
        icon: <SmileOutlined />,
        description: `你喜欢的操作`,
      },
      {
        key: '2-3',
        icon: <CommentOutlined />,
        description: `工具箱`,
      },
    ],
  },
];
const senderPromptsItems = [
  {
    key: '1',
    description: 'Hot Topics',
    icon: (
      <FireOutlined
        style={{
          color: '#FF4D4F',
        }}
      />
    ),
  },
  {
    key: '2',
    description: 'Design Guide',
    icon: (
      <ReadOutlined
        style={{
          color: '#1890FF',
        }}
      />
    ),
  },
];
const roles = {
  ai: {
    placement: 'start',
    avatar: {
      icon: <UserOutlined />,
      style: {
        background: '#fde3cf',
      },
    },
    typing: {
      step: 5,
      interval: 20,
    },
    styles: {
      content: {
        borderRadius: 16,
      },
    },
  },
  local: {
    placement: 'end',
    variant: 'shadow',
    avatar: {
      icon: <UserOutlined />,
      style: {
        background: '#87d068',
      },
    },
  },
};
const BaseCasePage = () => {

  const navigate = useNavigate()
  // ==================== Style ====================
  const { styles } = useStyle();
  const { caseID, memoryIDParam } = useParams();
  const [loading, setLoading] = React.useState(false);

  // ==================== State ====================
  const [headerOpen, setHeaderOpen] = React.useState(false);
  const [content, setContent] = React.useState('');
  const [conversationsItems, setConversationsItems] = React.useState(defaultConversationsItems);
  const [activeKey, setActiveKey] = React.useState(memoryIDParam ?? generateRandom32BitNumber());
  const [attachedFiles, setAttachedFiles] = React.useState([]);
  const [memoryID, setMemoryID] = useState(memoryIDParam ?? generateRandom32BitNumber());
  const [isNewMemory, setIsNewMemory] = useState(memoryIDParam == undefined ? true : false)
  const [caseInfo, setCaseInfo] = useState('');

  // ==================== Runtime ====================
  const [agent] = useXAgent({
    request: async ({ message }, { onSuccess, onUpdate }) => {
      var textString = ''
      await caseApi.streamMemory(
        {
          userInput: message,
          memoryID,
          isNewMemory,
          caseID,
          userID: "test"
        },
        (text) => {
          const json = JSON.parse(text)
          console.log(json.memoryID)

          textString += json.content;
          if (json.status === 'INPROCESS') {
            onUpdate();
          } else {

            onSuccess(textString)
          }
        })
      setIsNewMemory(!isNewMemory)
    },
  });
  const { onRequest, messages, setMessages } = useXChat({
    agent,
  });

  useEffect(() => {
    caseApi.queryAll({
      caseID
    }).then(data => {
      setCaseInfo(data.data.data.case.htmlContent ?? data.data.data.case.content)
    })

    caseApi.queryChatList({
      caseID,
      userID: 'test'
    }).then(data => {
      // console.log(`queryChatList:${JSON.stringify(data.data.data)}`)
      const a = []
      data.data.data.chatList.map(item => {
        a.push({
          key: item.id,
          label: item.brief ?? item.id
        })
      })
      setConversationsItems([
        ...conversationsItems,
        ...a
      ]);
    })

    if (memoryIDParam) {
      queryChatContentList(memoryIDParam)
    }

  }, [])

  const queryChatContentList = (memoryID) => {
    caseApi.queryChatContentList({
      memoryID
    }).then(data => {
      console.log(`queryChatContentList:${JSON.stringify(data.data.data)}`)
      const a = [];
      data.data.data.chatContentList.map(item => {
        a.push({
          id: item.id,
          isHistory: true,
          status: item.role === 'USER' ? 'local' : 'ai',
          message: item.content,
        })
      })
      setMessages([
        ...a
      ])
    })
  }

  useEffect(() => {
    if (activeKey !== undefined) {
      // setMessages([]);
      queryChatContentList(activeKey)
      agent.request = async ({ message }, { onSuccess, onUpdate }) => {
        var textString = ''
        await caseApi.streamMemory(
          {
            userInput: message,
            memoryID: activeKey,
            isNewMemory,
            caseID,
            userID: "test"
          },
          (text) => {
            const json = JSON.parse(text)
            console.log(json.memoryID)

            textString += json.content;
            if (json.status === 'INPROCESS') {
              onUpdate();
            } else {
              onSuccess(textString)
            }
          })
      }
    }
  }, [activeKey]);

  const toolChat = () => {
    console.log('触发了')
    agent.request = async ({ message }, { onSuccess, onUpdate }) => {
      var textString = ''
      await caseApi.toolMemory(
        {
          userInput: message,
          memoryID: activeKey,
          isNewMemory,
          caseID,
          userID: "test"
        },
        (text) => {
          const json = JSON.parse(text)
          console.log(json.memoryID)

          textString += json.content;
          if (json.status === 'INPROCESS') {
            onUpdate();
          } else {
            onSuccess(textString)
          }
        })
    }
  }

  const renderMarkdown = (content) => (
    <Typography>
      {/* biome-ignore lint/security/noDangerouslySetInnerHtml: used in demo */}
      <div
        dangerouslySetInnerHTML={{
          __html: md.render(content),
        }}
      />
    </Typography>
  );

  // ==================== Event ====================
  const onSubmit = (nextContent) => {
    if (!nextContent) return;
    onRequest(nextContent);
    setContent('');
  };

  const onPromptsItemClick = (info) => {
    onRequest(info.data.description);
  };

  const onAddConversation = () => {
    const newMemoryID = generateRandom32BitNumber();

    setConversationsItems([
      ...conversationsItems,
      {
        key: `${newMemoryID}`,
        label: `新对话`,
      },
    ]);
    setActiveKey(newMemoryID);
  };

  const onConversationClick = (key) => {
    setActiveKey(key);
    // queryChatContentList(key);
    // window.location.href = `/case/${caseID}/${key}`
  };

  const handleFileChange = (info) => setAttachedFiles(info.fileList);

  // ==================== Nodes ====================
  const placeholderNode = (
    <Space direction="vertical" size={16} className={styles.placeholder}>
      <Welcome
        variant="borderless"
        icon="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*s5sNRo5LjfQAAAAAAAAAAAAADgCCAQ/fmt.webp"
        title="Hello，这是LLW 智算大脑"
        description="基于大语言模型，为您提供智能法律服务的功能"
        extra={
          <Space>
            <Button icon={<ShareAltOutlined />} />
            <Button icon={<EllipsisOutlined />} />
          </Space>
        }
      />
      <Prompts
        title="猜你要想？"
        items={placeholderPromptsItems}
        styles={{
          list: {
            width: '100%',
          },
          item: {
            flex: 1,
          },
        }}
        onItemClick={onPromptsItemClick}
      />
    </Space>
  );
  const items = messages.map(({ id, message, status, isHistory }) => ({
    key: id,
    typing: (!isHistory && status !== 'local') ? true : false,
    loading: status === 'loading',
    role: status === 'local' ? 'local' : 'ai',
    content: message,
    messageRender: renderMarkdown
  }));
  const attachmentsNode = (
    <Badge dot={attachedFiles.length > 0 && !headerOpen}>
      <Button type="text" icon={<PaperClipOutlined />} onClick={() => setHeaderOpen(!headerOpen)} />
    </Badge>
  );
  const senderHeader = (
    <Sender.Header
      title="Attachments"
      open={headerOpen}
      onOpenChange={setHeaderOpen}
      styles={{
        content: {
          padding: 0,
        },
      }}
    >
      <Attachments
        beforeUpload={() => false}
        items={attachedFiles}
        onChange={handleFileChange}
        placeholder={(type) =>
          type === 'drop'
            ? {
              title: 'Drop file here',
            }
            : {
              icon: <CloudUploadOutlined />,
              title: 'Upload files',
              description: 'Click or drag files to this area to upload',
            }
        }
      />
    </Sender.Header>
  );
  const logoNode = (
    <div className={styles.logo}>
      <img
        src={LOGO}
        draggable={false}
        alt="logo"
      />
      <span>LLW 智算大脑</span>
    </div>
  );

  // ==================== Render =================
  return (
    <div className={styles.layout}>
      <div className={styles.menu}>
        {/* 🌟 Logo */}
        {logoNode}
        {/* 🌟 添加会话 */}
        <Button
          onClick={onAddConversation}
          type="link"
          className={styles.addBtn}
          icon={<PlusOutlined />}
        >
          New Conversation
        </Button>
        {/* 🌟 会话管理 */}
        <Conversations
          items={conversationsItems}
          className={styles.conversations}
          activeKey={activeKey}
          onActiveChange={onConversationClick}
        />
      </div>
      <div className={styles.chat}>
        {/* 🌟 消息列表 */}
        <Bubble.List
          items={
            items.length > 0
              ? items
              : [
                {
                  content: placeholderNode,
                  variant: 'borderless'
                },
              ]
          }
          roles={roles}
          className={styles.messages}
        />
        {/* 🌟 提示词 */}
        <Prompts items={senderPromptsItems} onItemClick={onPromptsItemClick} />
        {/* 🌟 输入框 */}
        <Sender
          value={content}
          header={senderHeader}
          onSubmit={onSubmit}
          onChange={setContent}
          prefix={attachmentsNode}
          loading={agent.isRequesting()}
          className={styles.sender}
          actions={
            (_, info) => {
              const { SendButton, LoadingButton, ClearButton } = info.components;

              return (
                <Space size="small">
                  <Button icon={<ToolOutlined />} onClick={toolChat} />
                  {loading ? (
                    <LoadingButton type="default" icon={<Spin size="small" />} disabled />
                  ) : (
                    <SendButton type="primary" icon={<ArrowUpOutlined />} disabled={false} />
                  )}
                </Space>
              );
            }
          }
        />
      </div>
      <div style={{ height: '100%', width: '35%', overflow: 'auto', paddingTop: '24px', paddingBottom: '24px' }} >
        <div style={{ height: '100%', overflow: 'scroll' }} dangerouslySetInnerHTML={{ __html: caseInfo }}>

        </div>
      </div>
    </div>
  );
}

export default BaseCasePage