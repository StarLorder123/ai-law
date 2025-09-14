import { Request, Response, NextFunction } from "express";
import { baseChat } from "../service/chat.service";
import { ChatDatabase } from '../db/chatDbService';

export const chatStream = (req: Request, res: Response, next: NextFunction) => {
    try {
        baseChat(req, res);
    } catch (error) {
        next(error);
    }
}

/**
 * 获取某个项目下的所有会话列表
 * @param req 请求对象
 * @param res 响应对象
 */
export const getSessionList = (req: Request, res: Response, next: NextFunction) => {
    try {
        const { projectid } = req.body;
        const userid = req.headers['user-id'] as string || 'default';

        if (!projectid) {
            res.json({ error: '缺少项目ID参数' });
            return;
        }

        const chatDb = new ChatDatabase(userid);
        const sessions = chatDb.getSessionDetails(projectid);
        chatDb.close();

        res.json({
            success: true,
            data: sessions
        });
    } catch (error) {
        console.error('获取会话列表失败:', error);
        next(error);
    }
};

/**
 * 获取某个会话的聊天记录
 * @param req 请求对象
 * @param res 响应对象
 */
export const getSessionChat = (req: Request, res: Response, next: NextFunction) => {
    try {
        const { projectid, sessionid } = req.body;
        const userid = req.headers['user-id'] as string || 'default';

        if (!projectid || !sessionid) {
            res.json({ error: '缺少必要参数' });
            return;
        }

        const chatDb = new ChatDatabase(userid);
        const chatRecords = chatDb.getChatRecordsByProjectAndSession(projectid, sessionid);
        chatDb.close();

        res.json({
            success: true,
            data: chatRecords
        });
    } catch (error) {
        console.error('获取聊天记录失败:', error);
        next(error);
    }
};