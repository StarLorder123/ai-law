import { ObjectId } from 'mongodb';
import { dbManager } from '../config/database';
import { Template } from '../interfaces/common';

/** 模板示例
 * {
  "_id": "template123",
  "name": "民事案件目录模板",
  "description": "适用于民事案件的通用文书模板",
  "version": 1,
  "created_by": "adminUser",
  "created_at": "2025-04-11T10:30:00.000Z",
  "items": {
    "item1": {
      "index": 1,
      "title": "起诉状",
      "required": true,
      "category": "文书",
      "description": "原告提交的起诉文件"
    },
    "item2": {
      "index": 2,
      "title": "证据清单",
      "required": false,
      "category": "证据",
      "description": "原告或被告提交的证据明细"
    }
  }
}
 */
class TemplateDao {
    private collection: string = 'templates';

    // 创建新模板
    async create(template: Omit<Template, '_id'>): Promise<ObjectId> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).insertOne(template);
        return result.insertedId;
    }

    // 根据ID获取模板
    async findById(id: string): Promise<Template | null> {
        const db = await this.getDb();
        return await db.collection(this.collection).findOne<Template>({
            _id: new ObjectId(id)
        });
    }

    // 获取所有模板
    async findAll(): Promise<Template[]> {
        const db = await this.getDb();
        return await db.collection(this.collection)
            .find<Template>({})
            .toArray();
    }

    // 根据名称查找模板
    async findByName(name: string): Promise<Template | null> {
        const db = await this.getDb();
        return await db.collection(this.collection).findOne<Template>({
            name: name
        });
    }

    // 更新模板
    async update(id: string, template: Partial<Template>): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).updateOne(
            { _id: new ObjectId(id) },
            { $set: template }
        );
        return result.modifiedCount > 0;
    }

    // 删除模板
    async delete(id: string): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).deleteOne({
            _id: new ObjectId(id)
        });
        return result.deletedCount > 0;
    }

    // 根据版本号查找模板
    async findByVersion(version: number): Promise<Template[]> {
        const db = await this.getDb();
        return await db.collection(this.collection)
            .find<Template>({ version: version })
            .toArray();
    }

    // 根据创建者查找模板
    async findByCreator(createdBy: string): Promise<Template[]> {
        const db = await this.getDb();
        return await db.collection(this.collection)
            .find<Template>({ created_by: createdBy })
            .toArray();
    }

    private async getDb() {
        return dbManager.getDb();
    }
}

export const templateDao = new TemplateDao();