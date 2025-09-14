import { ObjectId } from 'mongodb';
import { templateDao } from '../../src/dao/templateDao';
import { dbManager } from '../../src/config/database';

describe('TemplateDao', () => {
    beforeAll(async () => {
        await dbManager.connect('mongodb://localhost:27017', 'ai_law_test');
    });

    afterAll(async () => {
        await dbManager.close();
    });

    beforeEach(async () => {
        const db = dbManager.getDb();
        await db.collection('templates').deleteMany({});
    });

    const mockTemplate = {
        name: "民事案件目录模板",
        description: "适用于民事案件的通用文书模板",
        version: 1,
        created_by: "adminUser",
        created_at: new Date('2025-04-11T10:30:00.000Z'),
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
                title: "证据清单",
                required: false,
                category: "证据",
                description: "原告或被告提交的证据明细"
            }
        }
    };

    describe('create', () => {
        it('should create a new template', async () => {
            const id = await templateDao.create(mockTemplate);
            expect(id).toBeDefined();
            expect(id).toBeInstanceOf(ObjectId);
        });
    });

    describe('findById', () => {
        it('should find template by id', async () => {
            const id = await templateDao.create(mockTemplate);
            const result = await templateDao.findById(id.toString());
            
            expect(result).toBeDefined();
            expect(result?.name).toBe(mockTemplate.name);
            expect(result?.version).toBe(mockTemplate.version);
            expect(result?.items.item1.title).toBe(mockTemplate.items.item1.title);
        });
    });

    describe('findByName', () => {
        it('should find template by name', async () => {
            await templateDao.create(mockTemplate);
            const result = await templateDao.findByName(mockTemplate.name);

            expect(result).toBeDefined();
            expect(result?.name).toBe(mockTemplate.name);
        });
    });

    describe('findByVersion', () => {
        it('should find templates by version', async () => {
            await templateDao.create(mockTemplate);
            const results = await templateDao.findByVersion(1);

            expect(results).toHaveLength(1);
            expect(results[0].version).toBe(1);
        });
    });

    describe('update', () => {
        it('should update template', async () => {
            const id = await templateDao.create(mockTemplate);
            const updateResult = await templateDao.update(id.toString(), {
                version: 2
            });

            expect(updateResult).toBe(true);

            const updated = await templateDao.findById(id.toString());
            expect(updated?.version).toBe(2);
        });
    });

    describe('delete', () => {
        it('should delete template', async () => {
            const id = await templateDao.create(mockTemplate);
            const deleteResult = await templateDao.delete(id.toString());

            expect(deleteResult).toBe(true);

            const result = await templateDao.findById(id.toString());
            expect(result).toBeNull();
        });
    });
});