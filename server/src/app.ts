import dotenv from 'dotenv';
import express from "express";
import routes from "./routes";
import { errorHandler } from "./middlewares/errorHandler";

// 从.env文件中读取项目配置
dotenv.config();

const app = express();

// 中间件
app.use(express.json());

// 挂载路由
app.use("/api/v1", routes);

// 全局错误处理
app.use(errorHandler);

const PORT = process.env.SERVERPORT ?? 11099;

app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});