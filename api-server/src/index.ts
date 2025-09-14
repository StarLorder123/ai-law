import express from 'express';
import path from 'path';
import uploadRouter from './routes/upload';
import fileRouter from './routes/file';

const app = express();
const port = process.env.PORT || 3000;

// 静态文件服务
app.use('/uploads', express.static(path.join(process.cwd(), 'uploads')));

// 路由
app.use('/api/files', fileRouter);
app.use('/api/upload', uploadRouter);

app.listen(port, () => {
  console.log(`服务器运行在 http://localhost:${port}`);
});