/* eslint-disable @typescript-eslint/no-explicit-any */
import { archiveService } from '../../src/services/archiveService';
import { templateDao } from '../../src/dao/templateDao';
import { catalogDao } from '../../src/dao/catalogDao';
import { fileMetadataDao } from '../../src/dao/fileMetadataDao';
import { deleteFile } from '../../src/utils/fileUtil';

// Mock 依赖
jest.mock('../../src/dao/templateDao');
jest.mock('../../src/dao/catalogDao');
jest.mock('../../src/dao/fileMetadataDao');
jest.mock('../../src/utils/fileUtil');

describe('ArchiveService', () => {
    const mockFile: Express.Multer.File = {
        fieldname: 'file',
        originalname: 'test.pdf',
        encoding: '7bit',
        mimetype: 'application/pdf',
        buffer: Buffer.from('test file content'),
        size: 100,
        destination: '',
        filename: '',
        path: '',
        stream: null as any
    };

    const mockTemplate = {
        _id: 'template123',
        name: '民事案件模板',
        description: '测试模板',
        version: 1,
        created_by: 'admin',
        created_at: new Date(),
        items: {
            'item1': {
                index: 1,
                title: '起诉状',
                required: true,
                category: '文书',
                description: '原告提交的起诉文件'
            }
        }
    };

    beforeEach(() => {
        jest.clearAllMocks();
        
        // Mock templateDao
        (templateDao.findById as jest.Mock).mockResolvedValue(mockTemplate);

        // Mock fileMetadataDao
        (fileMetadataDao.create as jest.Mock).mockResolvedValue('file123');
        // 添加 delete 方法的 mock
        (fileMetadataDao.delete as jest.Mock).mockResolvedValue(true);

        // Mock catalogDao
        (catalogDao.findByCaseId as jest.Mock).mockResolvedValue(null);
        (catalogDao.create as jest.Mock).mockResolvedValue('catalog123');
        (catalogDao.findById as jest.Mock).mockResolvedValue({
            _id: 'catalog123',
            case_id: 'case123',
            template_id: 'template123',
            files: []
        });
        (catalogDao.addFile as jest.Mock).mockResolvedValue(true);
    });

    describe('archiveFile', () => {
        it('应该成功归档文件', async () => {
            const result = await archiveService.archiveFile(
                mockFile,
                'template123',
                'item1',
                'case123'
            );

            expect(result).toEqual({
                fileId: 'file123',
                catalogId: 'catalog123'
            });

            // 验证文件元数据创建
            expect(fileMetadataDao.create).toHaveBeenCalled();

            // 验证模板检查
            expect(templateDao.findById).toHaveBeenCalledWith('template123');

            // 验证卷宗创建和更新
            expect(catalogDao.create).toHaveBeenCalled();
            expect(catalogDao.addFile).toHaveBeenCalled();
        });

        it('当模板不存在时应该抛出错误', async () => {
            (templateDao.findById as jest.Mock).mockResolvedValue(null);

            await expect(
                archiveService.archiveFile(mockFile, 'invalid-template', 'item1', 'case123')
            ).rejects.toThrow('Template not found');

            // 验证文件清理
            expect(deleteFile).toHaveBeenCalled();
        });

        it('当item不存在时应该抛出错误', async () => {
            await expect(
                archiveService.archiveFile(mockFile, 'template123', 'invalid-item', 'case123')
            ).rejects.toThrow('Item key not found');

            expect(deleteFile).toHaveBeenCalled();
        });

        it('应该使用现有的卷宗而不是创建新的', async () => {
            const existingCatalog = {
                _id: 'existing-catalog',
                case_id: 'case123',
                template_id: 'template123',
                files: []
            };
            (catalogDao.findByCaseId as jest.Mock).mockResolvedValue(existingCatalog);

            const result = await archiveService.archiveFile(
                mockFile,
                'template123',
                'item1',
                'case123'
            );

            expect(result.catalogId).toBe('existing-catalog');
            expect(catalogDao.create).not.toHaveBeenCalled();
            expect(catalogDao.addFile).toHaveBeenCalled();
        });

        it('应该在发生错误时清理文件', async () => {
            (catalogDao.addFile as jest.Mock).mockRejectedValue(new Error('Database error'));

            await expect(
                archiveService.archiveFile(mockFile, 'template123', 'item1', 'case123')
            ).rejects.toThrow('Database error');

            expect(deleteFile).toHaveBeenCalled();
        });
    });
});