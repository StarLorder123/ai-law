import { ObjectId } from 'mongodb';
import { dbManager } from '../config/database';
import { FileMetadata } from '../interfaces/common';

/**
 * 元数据示例
 * {
  "_id": "6617909ce1e0fc1b456abcde",
  "file_name": "contract_v2.pdf",
  "type": "pdf",
  "size": 34823,
  "create_time": "2025-04-11T10:45:00.000Z",
  "storage_path": "2025/04/11/contract_v2.pdf"
}

 */
class FileMetadataDao {
    private collection: string = 'file_metadata';

    // 创建新的文件元数据记录
    async create(metadata: Omit<FileMetadata, '_id'>): Promise<ObjectId> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).insertOne(metadata);
        return result.insertedId;
    }

    // 根据ID获取文件元数据
    async findById(id: string): Promise<FileMetadata | null> {
        const db = await this.getDb();
        return await db.collection(this.collection).findOne<FileMetadata>({
            _id: new ObjectId(id)
        });
    }

    // 获取所有文件元数据
    async findAll(): Promise<FileMetadata[]> {
        const db = await this.getDb();
        return await db.collection(this.collection)
            .find<FileMetadata>({})
            .toArray();
    }

    // 更新文件元数据
    async update(id: string, metadata: Partial<FileMetadata>): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).updateOne(
            { _id: new ObjectId(id) },
            { $set: metadata }
        );
        return result.modifiedCount > 0;
    }

    // 删除文件元数据
    async delete(id: string): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).deleteOne({
            _id: new ObjectId(id)
        });
        return result.deletedCount > 0;
    }

    // 根据文件名查找
    async findByFileName(fileName: string): Promise<FileMetadata | null> {
        const db = await this.getDb();
        return await db.collection(this.collection).findOne<FileMetadata>({
            file_name: fileName
        });
    }

    private async getDb() {
        return dbManager.getDb();
    }
}

export const fileMetadataDao = new FileMetadataDao();