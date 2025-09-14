import { ObjectId } from 'mongodb';
import { dbManager } from '../config/database';
import { Catalog, CatalogFileItem } from '../interfaces/common';

class CatalogDao {
    private collection: string = 'catalogs';

    // 创建新的卷宗实例
    async create(catalog: Omit<Catalog, '_id'>): Promise<ObjectId> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).insertOne(catalog);
        return result.insertedId;
    }

    // 根据ID获取卷宗实例
    async findById(id: string): Promise<Catalog | null> {
        const db = await this.getDb();
        return await db.collection(this.collection).findOne<Catalog>({
            _id: new ObjectId(id)
        });
    }

    // 根据案件ID获取卷宗实例
    async findByCaseId(caseId: string): Promise<Catalog | null> {
        const db = await this.getDb();
        return await db.collection(this.collection).findOne<Catalog>({
            case_id: caseId
        });
    }

    // 根据模板ID获取所有卷宗实例
    async findByTemplateId(templateId: string): Promise<Catalog[]> {
        const db = await this.getDb();
        return await db.collection(this.collection)
            .find<Catalog>({ template_id: templateId })
            .toArray();
    }

    // 获取所有卷宗实例
    async findAll(): Promise<Catalog[]> {
        const db = await this.getDb();
        return await db.collection(this.collection)
            .find<Catalog>({})
            .toArray();
    }

    // 更新卷宗实例
    async update(id: string, catalog: Partial<Catalog>): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).updateOne(
            { _id: new ObjectId(id) },
            { $set: catalog }
        );
        return result.modifiedCount > 0;
    }

    // 删除卷宗实例
    async delete(id: string): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).deleteOne({
            _id: new ObjectId(id)
        });
        return result.deletedCount > 0;
    }

    // 根据时间范围查找卷宗实例
    async findByDateRange(startDate: Date, endDate: Date): Promise<Catalog[]> {
        const db = await this.getDb();
        return await db.collection(this.collection)
            .find<Catalog>({
                generated_at: {
                    $gte: startDate,
                    $lte: endDate
                }
            })
            .toArray();
    }

    private async getDb() {
        return dbManager.getDb();
    }

    // 添加文件到卷宗
    async addFile(catalogId: string, fileItem: CatalogFileItem): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).updateOne(
            { _id: new ObjectId(catalogId) },
            { 
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                $push: { files: fileItem } as any,
                $set: { last_modified: new Date() }
            }
        );
        return result.modifiedCount > 0;
    }

    // 更新文件状态
    async updateFileStatus(catalogId: string, fileId: string, status: CatalogFileItem['status'], remarks?: string): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).updateOne(
            { 
                _id: new ObjectId(catalogId),
                'files.file_id': fileId 
            },
            { 
                $set: { 
                    'files.$.status': status,
                    'files.$.remarks': remarks,
                    last_modified: new Date()
                }
            }
        );
        return result.modifiedCount > 0;
    }

    // 移除文件
    async removeFile(catalogId: string, fileId: string): Promise<boolean> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).updateOne(
            { _id: new ObjectId(catalogId) },
            { 
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                $pull: { 'files': { 'file_id': fileId } } as any,
                $set: { last_modified: new Date() }
            }
        );
        return result.modifiedCount > 0;
    }

    // 根据文件ID查找卷宗
    async findByFileId(fileId: string): Promise<Catalog | null> {
        const db = await this.getDb();
        return await db.collection(this.collection).findOne<Catalog>({
            'files.file_id': fileId
        });
    }

    // 获取特定模板项的所有文件
    async getFilesByItemKey(catalogId: string, itemKey: string): Promise<CatalogFileItem[]> {
        const db = await this.getDb();
        const result = await db.collection(this.collection).findOne<Catalog>(
            { _id: new ObjectId(catalogId) },
            { projection: { files: 1 } }
        );
        return result?.files.filter(file => file.item_key === itemKey) || [];
    }
}

export const catalogDao = new CatalogDao();