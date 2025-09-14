import express from 'express';
import { archiveService } from '../services/archiveService';

const router = express.Router();

// 通过ID获取文件
router.get('/:fileId', async (req: express.Request, res: express.Response): Promise<void> => {
    try {
        const { fileId } = req.params;
        const result = await archiveService.getFileById(fileId);
        
        // 设置响应头
        res.setHeader('Content-Type', `application/${result.metadata.type}`);
        res.setHeader('Content-Disposition', `attachment; filename=${result.metadata.file_name}`);
        res.setHeader('Content-Length', result.metadata.size);

        // 发送文件内容
        res.send(result.content);
    } catch (error) {
        res.status(404).json({
            success: false,
            message: error instanceof Error ? error.message : '文件获取失败'
        });
    }
});

export default router;