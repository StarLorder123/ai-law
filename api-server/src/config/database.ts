import { MongoClient, Db } from 'mongodb';

class DatabaseManager {
    private static instance: DatabaseManager;
    private client: MongoClient | null = null;
    private db: Db | null = null;

    private constructor() {}

    static getInstance(): DatabaseManager {
        if (!DatabaseManager.instance) {
            DatabaseManager.instance = new DatabaseManager();
        }
        return DatabaseManager.instance;
    }

    async connect(url: string, dbName: string): Promise<void> {
        if (!this.client) {
            // 添加身份验证信息
            const authUrl = url.replace('mongodb://', 'mongodb://root:NJU123456@');
            this.client = await MongoClient.connect(authUrl);
            this.db = this.client.db(dbName);
            console.log('MongoDB connected successfully');
        }
    }

    getDb(): Db {
        if (!this.db) {
            throw new Error('Database not connected');
        }
        return this.db;
    }

    async close(): Promise<void> {
        if (this.client) {
            await this.client.close();
            this.client = null;
            this.db = null;
        }
    }
}

export const dbManager = DatabaseManager.getInstance();