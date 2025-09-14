import { ObjectId } from 'mongodb';
import { catalogDao } from '../../src/dao/catalogDao';
import { dbManager } from '../../src/config/database';

describe('CatalogDao', () => {
    beforeAll(async () => {
        await dbManager.connect('mongodb://localhost:27017', 'ai_law_test');
    });

    afterAll(async () => {
        await dbManager.close();
    });

    const mockCatalog = {
        case_id: "CASE-20250401-XYZ",
        template_id: "template-123",
        generated_at: new Date('2025-04-11T11:00:00.000Z'),
        template: {
            id: "template-123",
            name: "民事案件通用模板",
            description: "适用于一般民事案件的电子卷宗结构",
            version: 1,
            created_by: "admin",
            created_at: new Date('2025-04-10T08:30:00.000Z'),
            items: {
                item1: {
                    index: 1,
                    title: "起诉状",
                    required: true,
                    category: "文书",
                    description: "原告提交的起诉文件"
                },
                item2: {
                    index: 2,
                    title: "证据材料",
                    required: false,
                    category: "证据",
                    description: "提交的证据清单与原件"
                }
            }
        },
        files: [],  // 初始化空文件数组
        status: 'draft' as const,
        last_modified: new Date('2025-04-11T11:00:00.000Z')
    };

    const mockFileItem = {
        file_id: "file-123",
        item_key: "item1",
        upload_time: new Date('2025-04-11T11:30:00.000Z'),
        status: 'pending' as const
    };

    describe('create', () => {
        it('should create a new catalog', async () => {
            const id = await catalogDao.create(mockCatalog);
            expect(id).toBeDefined();
            expect(id).toBeInstanceOf(ObjectId);
        });
    });

    describe('findById', () => {
        it('should find catalog by id', async () => {
            const id = await catalogDao.create(mockCatalog);
            const result = await catalogDao.findById(id.toString());
            
            expect(result).toBeDefined();
            expect(result?.case_id).toBe(mockCatalog.case_id);
            expect(result?.template.name).toBe(mockCatalog.template.name);
        });
    });

    describe('findByCaseId', () => {
        it('should find catalog by case id', async () => {
            await catalogDao.create(mockCatalog);
            const result = await catalogDao.findByCaseId(mockCatalog.case_id);

            expect(result).toBeDefined();
            expect(result?.case_id).toBe(mockCatalog.case_id);
        });
    });

    describe('findByTemplateId', () => {
        it('should find catalogs by template id', async () => {
            await catalogDao.create(mockCatalog);
            const results = await catalogDao.findByTemplateId(mockCatalog.template_id);

            expect(results.length).toBeGreaterThan(0);
            expect(results[0].template_id).toBe(mockCatalog.template_id);
        });
    });

    describe('update', () => {
        it('should update catalog', async () => {
            const id = await catalogDao.create(mockCatalog);
            const updateResult = await catalogDao.update(id.toString(), {
                case_id: "CASE-20250401-ABC"
            });

            expect(updateResult).toBe(true);

            const updated = await catalogDao.findById(id.toString());
            expect(updated?.case_id).toBe("CASE-20250401-ABC");
        });
    });

    describe('delete', () => {
        it('should delete catalog', async () => {
            const id = await catalogDao.create(mockCatalog);
            const deleteResult = await catalogDao.delete(id.toString());

            expect(deleteResult).toBe(true);

            const result = await catalogDao.findById(id.toString());
            expect(result).toBeNull();
        });
    });

    describe('findByDateRange', () => {
        it('should find catalogs within date range', async () => {
            await catalogDao.create(mockCatalog);
            
            const startDate = new Date('2025-04-11T00:00:00.000Z');
            const endDate = new Date('2025-04-12T00:00:00.000Z');
            
            const results = await catalogDao.findByDateRange(startDate, endDate);
            
            expect(results.length).toBeGreaterThan(0);
            expect(results[0].generated_at.getTime()).toBeGreaterThanOrEqual(startDate.getTime());
            expect(results[0].generated_at.getTime()).toBeLessThanOrEqual(endDate.getTime());
        });
    });

    describe('addFile', () => {
        it('should add file to catalog', async () => {
            const id = await catalogDao.create(mockCatalog);
            const addResult = await catalogDao.addFile(id.toString(), mockFileItem);

            expect(addResult).toBe(true);

            const updated = await catalogDao.findById(id.toString());
            expect(updated?.files).toHaveLength(1);
            expect(updated?.files[0].file_id).toBe(mockFileItem.file_id);
        });
    });

    describe('updateFileStatus', () => {
        it('should update file status', async () => {
            const id = await catalogDao.create({
                ...mockCatalog,
                files: [mockFileItem]
            });

            const updateResult = await catalogDao.updateFileStatus(
                id.toString(),
                mockFileItem.file_id,
                'approved',
                '文件已审核通过'
            );

            expect(updateResult).toBe(true);

            const updated = await catalogDao.findById(id.toString());
            expect(updated?.files[0].status).toBe('approved');
            expect(updated?.files[0].remarks).toBe('文件已审核通过');
        });
    });

    describe('removeFile', () => {
        it('should remove file from catalog', async () => {
            const id = await catalogDao.create({
                ...mockCatalog,
                files: [mockFileItem]
            });

            const removeResult = await catalogDao.removeFile(id.toString(), mockFileItem.file_id);

            expect(removeResult).toBe(true);

            const updated = await catalogDao.findById(id.toString());
            expect(updated?.files).toHaveLength(0);
        });
    });

    describe('findByFileId', () => {
        it('should find catalog by file id', async () => {
            const id = await catalogDao.create({
                ...mockCatalog,
                files: [mockFileItem]
            });

            const result = await catalogDao.findByFileId(mockFileItem.file_id);

            expect(result).toBeDefined();
            expect(result?._id?.toString()).toBe(id.toString());
        });
    });

    describe('getFilesByItemKey', () => {
        it('should get files by item key', async () => {
            const id = await catalogDao.create({
                ...mockCatalog,
                files: [mockFileItem]
            });

            const files = await catalogDao.getFilesByItemKey(id.toString(), 'item1');

            expect(files).toHaveLength(1);
            expect(files[0].item_key).toBe('item1');
        });

        it('should return empty array for non-existent item key', async () => {
            const id = await catalogDao.create({
                ...mockCatalog,
                files: [mockFileItem]
            });

            const files = await catalogDao.getFilesByItemKey(id.toString(), 'non-existent');

            expect(files).toHaveLength(0);
        });
    });
});