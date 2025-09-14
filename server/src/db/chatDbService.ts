import Database from 'better-sqlite3';
import path from 'path';
import fs from 'fs';
import { AIMessage, BaseMessage, HumanMessage, SystemMessage } from '@langchain/core/messages';

/**
 * 聊天数据库表：
 * id：聊天单条记录的唯一值
 * projectid：项目ID
 * sessionid：session ID，一轮对话的唯一ID
 * chat_object_type：聊天对象 ai system human
 * message：聊天具体内容
 * timestamp：生成时间
 */
const DEFAULT_DB_DIR = "db";

export class ChatDatabase {
    private db: Database.Database;
    private dbPath: string;

    constructor(userid: string) {
        // 根据 userid 构造数据库文件路径
        this.dbPath = path.join(DEFAULT_DB_DIR, `${userid}.db`);

        // 确保 resources 目录存在
        if (!fs.existsSync(DEFAULT_DB_DIR)) {
            fs.mkdirSync(DEFAULT_DB_DIR);
        }

        // 创建或打开数据库
        this.db = new Database(this.dbPath);
        this.initialize();
    }

    private initialize() {
        // 创建聊天记录表
        this.createChatTable();
    }

    // 创建聊天记录表
    private createChatTable() {
        const stmt = this.db.prepare(`
      CREATE TABLE IF NOT EXISTS chat_records (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        projectid TEXT NOT NULL,
        sessionid TEXT NOT NULL,
        chat_object_type TEXT NOT NULL,
        message TEXT NOT NULL,
        timestamp INTEGER NOT NULL
      )
    `);
        stmt.run();
    }

    // 插入一条聊天记录
    public insertChatRecord(projectid: string, sessionid: string, chatObjectType: string, message: string) {
        const stmt = this.db.prepare(`
      INSERT INTO chat_records (projectid, sessionid, chat_object_type, message, timestamp) 
      VALUES (?, ?, ?, ?, ?)
    `);
        stmt.run(projectid, sessionid, chatObjectType, message, Date.now());
    }

    // 根据 projectid 和 sessionid 查询聊天记录
    public getChatRecordsByProjectAndSession(projectid: string, sessionid: string) {
        const stmt = this.db.prepare(`
      SELECT * FROM chat_records WHERE projectid = ? AND sessionid = ?
    `);
        return stmt.all(projectid, sessionid);
    }

    // 查询根据 projectid 获取所有 sessionid 和每个 sessionid 的第一条消息
    public getSessionDetails(projectid: string) {
        const stmt = this.db.prepare(`
      SELECT sessionid, 
             (SELECT message FROM chat_records WHERE sessionid = s.sessionid ORDER BY timestamp LIMIT 1) AS first_message 
      FROM chat_records s 
      WHERE projectid = ? 
      GROUP BY sessionid
    `);
        return stmt.all(projectid);
    }

    // 关闭数据库连接
    public close() {
        this.db.close();
    }
}

/**
 * 从chat数据库中获取messages并返回
 * @param userid 
 * @param projectid 
 * @param sessionid 
 * @returns 
 */
export function getSessionFromDB(userid: string, projectid: string, sessionid: string) {
    const chatdb = new ChatDatabase(userid);
    const arr = chatdb.getChatRecordsByProjectAndSession(projectid, sessionid);
    const messages: BaseMessage[] = []
    for (const i in arr) {
        switch ((arr[i] as any).chat_object_type) {
            case 'ai':
                messages.push(new AIMessage((arr[i] as any).message));
            case 'human':
                messages.push(new HumanMessage((arr[i] as any).message));
            case 'system':
                messages.push(new SystemMessage((arr[i] as any).message));
        }
    }
    return messages;
}
