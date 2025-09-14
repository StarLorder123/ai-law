# 文件夹结构

api-server/
├── src/
│   ├── config/                 # 配置文件目录
│   │   ├── database.js        # 数据库配置
│   │   └── app.js            # 应用配置
│   ├── controllers/           # 控制器目录
│   │   └── userController.js  # 用户相关控制器
│   ├── models/                # 数据模型目录
│   │   └── userModel.js      # 用户模型
│   ├── routes/                # 路由目录
│   │   ├── index.js          # 路由汇总
│   │   └── userRoutes.js     # 用户相关路由
│   ├── services/              # 业务逻辑服务层
│   │   └── userService.js    # 用户相关服务
│   ├── middlewares/          # 中间件目录
│   │   ├── auth.js           # 认证中间件
│   │   └── errorHandler.js   # 错误处理中间件
│   ├── utils/                # 工具函数目录
│   │   └── helper.js         # 通用辅助函数
│   └── index.js              # 应用入口文件
├── tests/                    # 测试文件目录
├── .env                      # 环境变量
├── .gitignore               # Git忽略文件
├── package.json             # 项目配置文件
└── webpack.config.js        # Webpack配置文件