import { ObjectId } from 'mongodb';
import { fileMetadataDao } from '../../src/dao/fileMetadataDao';
import { dbManager } from '../../src/config/database';

describe('FileMetadataDao', () => {
    beforeAll(async () => {
        // 连接测试数据库
        await dbManager.connect('mongodb://localhost:27017', 'ai_law_test');
    });

    afterAll(async () => {
        // 断开数据库连接
        await dbManager.close();
    });

    beforeEach(async () => {
        // 清空集合数据
        const db = dbManager.getDb();
        await db.collection('file_metadata').deleteMany({});
    });

    const mockFileMetadata = {
        file_name: 'test.pdf',
        type: 'pdf',
        size: 12345,
        create_time: new Date('2025-04-11T10:45:00.000Z'),
        storage_path: '2025/04/11/test.pdf'
    };

    describe('create', () => {
        it('should create a new file metadata record', async () => {
            const id = await fileMetadataDao.create(mockFileMetadata);
            expect(id).toBeDefined();
            expect(id).toBeInstanceOf(ObjectId);
        });
    });

    describe('findById', () => {
        it('should find file metadata by id', async () => {
            const id = await fileMetadataDao.create(mockFileMetadata);
            const result = await fileMetadataDao.findById(id.toString());
            
            expect(result).toBeDefined();
            expect(result?.file_name).toBe(mockFileMetadata.file_name);
            expect(result?.type).toBe(mockFileMetadata.type);
            expect(result?.size).toBe(mockFileMetadata.size);
            expect(result?.storage_path).toBe(mockFileMetadata.storage_path);
        });

        it('should return null for non-existent id', async () => {
            const result = await fileMetadataDao.findById(new ObjectId().toString());
            expect(result).toBeNull();
        });
    });

    describe('findAll', () => {
        it('should return all file metadata records', async () => {
            // 创建第一条记录
            await fileMetadataDao.create({
                // 移除_id字段，让MongoDB自动生成
                ...mockFileMetadata,
                file_name: 'test1.pdf',
                storage_path: '2025/04/11/test1.pdf'
            });

            // 创建第二条记录，使用不同的数据
            await fileMetadataDao.create({
                ...mockFileMetadata,
                file_name: 'test2.pdf',
                size: 67890,
                create_time: new Date('2025-04-12T10:45:00.000Z'),
                storage_path: '2025/04/12/test2.pdf'
            });

            const results = await fileMetadataDao.findAll();
            expect(results).toHaveLength(2);
            
            expect(results.map(r => r.file_name).sort()).toEqual(['test1.pdf', 'test2.pdf']);
            expect(results.map(r => r.storage_path).sort()).toEqual(['2025/04/11/test1.pdf', '2025/04/12/test2.pdf']);
        });
    });

    describe('update', () => {
        it('should update file metadata', async () => {
            const id = await fileMetadataDao.create(mockFileMetadata);
            const updateResult = await fileMetadataDao.update(id.toString(), {
                size: 54321,
                storage_path: '2025/04/11/updated_test.pdf'
            });

            expect(updateResult).toBe(true);

            const updated = await fileMetadataDao.findById(id.toString());
            expect(updated?.size).toBe(54321);
            expect(updated?.storage_path).toBe('2025/04/11/updated_test.pdf');
        });
    });

    describe('delete', () => {
        it('should delete file metadata', async () => {
            const id = await fileMetadataDao.create(mockFileMetadata);
            const deleteResult = await fileMetadataDao.delete(id.toString());

            expect(deleteResult).toBe(true);

            const result = await fileMetadataDao.findById(id.toString());
            expect(result).toBeNull();
        });
    });

    describe('findByFileName', () => {
        it('should find file metadata by filename', async () => {
            await fileMetadataDao.create(mockFileMetadata);
            const result = await fileMetadataDao.findByFileName(mockFileMetadata.file_name);

            expect(result).toBeDefined();
            expect(result?.file_name).toBe(mockFileMetadata.file_name);
            expect(result?.storage_path).toBe(mockFileMetadata.storage_path);
        });

        it('should return null for non-existent filename', async () => {
            const result = await fileMetadataDao.findByFileName('non-existent.pdf');
            expect(result).toBeNull();
        });
    });
});