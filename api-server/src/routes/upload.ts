import express from 'express';
import multer from 'multer';
import path from 'path';
import { generateSafeFileName, saveFile } from '../utils/fileUtil';

const router = express.Router();

// 配置 multer
const upload = multer({
    storage: multer.memoryStorage(),
    limits: {
        fileSize: 10 * 1024 * 1024, // 限制文件大小为 10MB
    },
    fileFilter: (req, file, cb) => {
        // 允许的文件类型
        const allowedTypes = ['.jpg', '.jpeg', '.png', '.pdf', '.doc', '.docx'];
        const ext = path.extname(file.originalname).toLowerCase();

        if (allowedTypes.includes(ext)) {
            cb(null, true);
        } else {
            cb(new Error('不支持的文件类型'));
        }
    }
});

// 单文件上传接口
router.post('/single', upload.single('file'), async (req: express.Request, res: express.Response): Promise<void> => {
    try {
        if (!req.file) {
            res.status(400).json({ error: '没有上传文件' });
            return;
        }

        const safeFileName = generateSafeFileName(req.file.originalname);
        await saveFile(safeFileName, req.file.buffer);

        res.json({
            success: true,
            filename: safeFileName
        });
    } catch (error) {
        console.error('文件上传失败:', error);
        res.status(500).json({ error: '文件上传失败' });
    }
});

// 多文件上传接口
router.post('/multiple', upload.array('files', 5), async (req: express.Request, res: express.Response): Promise<void> => {
    try {
        if (!req.files || req.files.length === 0) {
            res.status(400).json({ error: '没有上传文件' });
            return;
        }

        const files = req.files as Express.Multer.File[];
        const uploadedFiles = await Promise.all(
            files.map(async (file) => {
                const safeFileName = generateSafeFileName(file.originalname);
                await saveFile(safeFileName, file.buffer);
                return safeFileName;
            })
        );

        res.json({
            success: true,
            files: uploadedFiles
        });
    } catch (error) {
        console.error('文件上传失败:', error);
        res.status(500).json({ error: '文件上传失败' });
    }
});

export default router;