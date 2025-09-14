# AI法律助手项目

这是一个基于人工智能的法律助手系统，核心是**llw项目**——一个完整的基于案件文书的AI对话式DEMO。其他子项目主要用于AI辅助案件文书整理功能的开发和实现。项目采用前后端分离架构，支持多种技术栈实现。

## 🏗️ 项目架构

```
ai-law/
├── llw/               # 🎯 核心项目：基于案件文书的AI对话式DEMO
│   ├── DataCrawl/     # 法律文档数据爬取和预处理
│   ├── llw-front/     # AI对话前端界面
│   └── llw-server/    # RAG后端服务 (LangChain4j + 向量数据库)
├── api-server/        # 📁 案件文书整理API服务器 (Node.js)
├── front/             # 📁 案件文书整理前端应用 (React)
├── java-server/       # 📁 案件文书整理API服务器 (Spring Boot)
├── server/            # 📁 AI聊天服务器 (LangChain)
└── data/             # 数据存储目录
```

## 📦 子系统介绍

### 🎯 核心项目：LLW - AI对话式法律助手 (`llw/`)

**这是项目的核心部分**，一个完整的基于案件文书的AI对话式DEMO系统。

#### 📊 数据处理层 (`llw/DataCrawl/`)
**技术栈**: Python

负责法律文档的预处理和数据准备：
- 📄 支持PDF、DOCX、TXT等多种格式解析
- 🔍 OCR文字识别功能
- 📝 文档内容分割和向量化预处理
- 🗂️ 批量文件处理和格式转换

**包含的案件文书样本**:
- 中华人民共和国刑法修正案（十二）
- 最高人民法院司法解释文件
- 典型民事判决书案例

#### 💬 AI对话前端 (`llw/llw-front/`)
**技术栈**: React 18 + Ant Design + Ant Design X

提供直观的AI对话界面，专门针对法律案件文书查询优化：
- 智能对话界面
- 案件文书检索展示
- 法律条文引用显示
- 响应式设计

#### 🧠 RAG智能后端 (`llw/llw-server/rag/`)
**技术栈**: Spring Boot 3.4 + LangChain4j + 多数据库架构

这是AI对话的核心引擎，实现了完整的RAG（检索增强生成）架构：

**🔍 智能检索能力**:
- **Milvus向量数据库**: 语义相似度检索
- **Neo4j图数据库**: 法律条文关系分析
- **PostgreSQL**: 结构化数据存储
- **MongoDB**: 文档元数据管理
- **Redis**: 对话上下文缓存

**🤖 AI对话能力**:
- 支持Ollama本地大模型部署
- 支持OpenAI API集成
- 中文法律语义理解（BGE-Small-ZH嵌入模型）
- 基于案件文书的智能问答

---

### 📁 辅助项目：案件文书整理系统

以下项目主要用于AI辅助案件文书整理功能的开发：

#### 1. Node.js API服务器 (`api-server/`)
**技术栈**: Node.js + TypeScript + Express + MongoDB

为案件文书整理提供后端API支持：
- 文件上传和存储管理
- 文档元数据处理
- 归档分类系统
- RESTful API接口

#### 2. React前端应用 (`front/`)
**技术栈**: React 19 + Vite + Ant Design

案件文书整理的管理界面：
- 📊 **仪表板**: 案件统计和概览
- 📁 **智能归档**: 文档自动分类
- 📋 **案件管理**: 案件信息维护
- 📄 **文档管理**: 文书上传和编辑
- 👥 **客户管理**: 当事人信息管理
- 📅 **日程管理**: 案件时间节点跟踪

#### 3. Java API服务器 (`java-server/`)
**技术栈**: Spring Boot 3.4.4 + MongoDB

企业级的案件文书处理服务：
- Spring Boot架构设计
- 文档解析和处理（Apache Tika）
- MongoDB数据持久化
- 文件类型识别和转换

#### 4. AI聊天服务器 (`server/`)
**技术栈**: Node.js + TypeScript + LangChain + SQLite

轻量级的AI对话服务：
- 基于LangChain的对话管理
- Ollama本地模型集成
- 对话历史存储
- 法律知识库查询

## 🚀 快速开始

### 🎯 体验核心AI对话DEMO

#### 环境要求
- Java 17+
- Node.js 18+
- Python 3.8+
- PostgreSQL (推荐)
- MongoDB
- Redis
- Milvus (向量数据库)
- Neo4j (可选)
- Ollama (本地AI模型)

#### 启动LLW核心系统

1. **准备数据**
```bash
cd llw/DataCrawl
pip install -r requirements.txt
python test.py  # 处理法律文档数据
```

2. **启动RAG后端服务**
```bash
cd llw/llw-server/rag
./mvnw spring-boot:run
```

3. **启动AI对话前端**
```bash
cd llw/llw-front
npm install
npm start
```

### 📁 开发案件文书整理功能

#### 启动辅助开发环境

1. **启动React管理界面**
```bash
cd front
npm install
npm run dev
```

2. **启动Node.js API服务**
```bash
cd api-server
npm install
npm run dev
```

3. **启动轻量级AI聊天**
```bash
cd server
npm install
npm run dev
```

4. **启动Java API服务** (可选)
```bash
cd java-server/ai_law_api_server
./mvnw spring-boot:run
```

## 🗄️ 数据库

项目使用多种数据库来满足不同的需求：

- **SQLite**: 存储对话历史和法律条文 (`data/laws_document.db`, `server/db/laws.db`)
- **MongoDB**: 文档元数据和用户数据
- **PostgreSQL**: 关系型数据存储 (RAG服务)
- **Milvus**: 向量数据库，用于语义搜索
- **Neo4j**: 图数据库，用于法律关系分析
- **Redis**: 缓存和会话管理

## 🔧 开发工具

- **构建工具**: Webpack, Vite, Maven
- **测试框架**: Jest, Spring Boot Test
- **代码规范**: ESLint, TypeScript
- **容器化**: 支持Docker部署
- **文档处理**: Apache Tika, PDFBox

## ✨ 核心特色

### 🎯 LLW AI对话DEMO的亮点功能

1. **🤖 智能案件文书问答**: 基于真实法律文档的精准问答
2. **🔍 语义检索**: Milvus向量数据库支持的语义相似度搜索
3. **📊 知识图谱**: Neo4j构建的法律条文关系网络
4. **🧠 RAG架构**: 检索增强生成，确保回答准确性
5. **🌐 多模型支持**: 同时支持Ollama本地模型和OpenAI API
6. **🎨 现代化界面**: 基于Ant Design X的对话式交互

### 📁 案件文书整理系统特性

1. **📂 智能归档**: AI辅助的文档自动分类
2. **📄 多格式支持**: PDF、DOCX、图片等格式的智能解析
3. **👥 协作管理**: 多用户案件协作处理
4. **📅 时间轴跟踪**: 案件进展的可视化管理
5. **🔒 安全存储**: 企业级的文档安全保护
6. **📱 响应式设计**: 支持移动端访问

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进项目。

## 📄 许可证

本项目采用MIT许可证，详见各子项目的许可证文件。

---

## 🎯 项目重点

- **核心价值**: `llw/` 目录包含完整的基于案件文书的AI对话式DEMO
- **开发支持**: 其他目录提供案件文书整理功能的开发框架和工具
- **技术栈**: 融合了现代前端、企业级后端、AI大模型等多种技术

**作者**: Lee Jinhao  
**项目类型**: AI法律助手系统  
**核心功能**: 基于案件文书的智能对话DEMO + 文书整理开发框架  
**最后更新**: 2025年9月
