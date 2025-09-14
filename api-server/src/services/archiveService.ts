import fs from 'fs/promises';
import path from 'path';
import { fileMetadataDao } from '../dao/fileMetadataDao';
import { templateDao } from '../dao/templateDao';
import { catalogDao } from '../dao/catalogDao';
import { FileMetadata, CatalogFileItem } from '../interfaces/common';
import { generateSafeFileName, saveFile, deleteFile } from '../utils/fileUtil';

class ArchiveService {

    // 智能归档处理
    async archiveFile(
        file: Express.Multer.File,
        templateId: string,
        itemKey: string,
        caseId: string
    ): Promise<{
        fileId: string;
        catalogId: string;
    }> {
        let savedFileName: string | null = null;
        let fileMetadata: FileMetadata | null = null;
        
        try {
            // 1. 生成安全的文件名并存储文件
            savedFileName = generateSafeFileName(file.originalname);
            await saveFile(savedFileName, file.buffer);

            // 2. 创建文件元信息
            fileMetadata = await this.createFileMetadata(file, savedFileName);

            // 3. 检查模板是否存在
            const template = await templateDao.findById(templateId);
            if (!template) {
                await deleteFile(savedFileName);  // 添加这行
                throw new Error(`Template not found: ${templateId}`);
            }

            // 4. 检查item是否存在于模板中
            if (!template.items[itemKey]) {
                await deleteFile(savedFileName);  // 添加这行
                throw new Error(`Item key not found in template: ${itemKey}`);
            }

            // 5. 查找或创建案件卷宗
            let catalog = await catalogDao.findByCaseId(caseId);
            if (!catalog) {
                const newCatalog = {
                    case_id: caseId,
                    template_id: templateId,
                    generated_at: new Date(),
                    template: {
                        id: template._id!.toString(),
                        name: template.name,
                        description: template.description,
                        version: template.version,
                        created_by: template.created_by,
                        created_at: template.created_at,
                        items: template.items
                    },
                    files: [],
                    status: 'draft' as const,
                    last_modified: new Date()
                };
                const catalogId = await catalogDao.create(newCatalog);
                catalog = await catalogDao.findById(catalogId.toString());
            }

            // 6. 创建文件归档项
            const fileItem: CatalogFileItem = {
                file_id: fileMetadata._id!.toString(),
                item_key: itemKey,
                upload_time: new Date(),
                status: 'pending'
            };

            // 7. 更新卷宗
            const addFileResult = await catalogDao.addFile(catalog!._id!.toString(), fileItem);
            if (!addFileResult) {
                throw new Error('Failed to add file to catalog');
            }

            return {
                fileId: fileMetadata._id!.toString(),
                catalogId: catalog!._id!.toString()
            };
        } catch (error) {
            // 确保在任何错误发生时都清理文件
            if (savedFileName) {
                await deleteFile(savedFileName).catch(() => {
                    console.error('Failed to delete file:', savedFileName);
                });
            }
            // 如果文件元数据已创建但后续步骤失败，也需要清理
            if (fileMetadata?._id) {
                await fileMetadataDao.delete(fileMetadata._id.toString()).catch(() => {
                    console.error('Failed to delete file metadata:', fileMetadata?._id);
                });
            }
            throw error;
        }
    }


    // 创建文件元信息
    private async createFileMetadata(
        file: Express.Multer.File, 
        savedFileName: string
    ): Promise<FileMetadata> {
        // 生成存储路径：按年/月/日组织
        const now = new Date();
        const storagePath = path.join(
            now.getFullYear().toString(),
            (now.getMonth() + 1).toString().padStart(2, '0'),
            now.getDate().toString().padStart(2, '0'),
            savedFileName
        ).replace(/\\/g, '/'); // 确保使用正斜杠

        const metadata: Omit<FileMetadata, '_id'> = {
            file_name: savedFileName,  // 使用安全的文件名
            type: path.extname(file.originalname).slice(1),
            size: file.size,
            create_time: new Date(),
            storage_path: storagePath
        };

        const id = await fileMetadataDao.create(metadata);
        return {
            _id: id,
            ...metadata
        };
    }

    // 通过文件ID获取文件
    async getFileById(fileId: string): Promise<{
        content: Buffer;
        metadata: FileMetadata;
    }> {
        // 获取文件元数据
        const metadata = await fileMetadataDao.findById(fileId);
        if (!metadata) {
            throw new Error(`文件元数据不存在: ${fileId}`);
        }

        try {
            // 读取文件内容
            const filePath = path.join(process.env.UPLOAD_DIR || 'uploads', metadata.storage_path);
            const content = await fs.readFile(filePath);

            return {
                content,
                metadata
            };
        } catch (error) {
            throw new Error(`读取文件失败: ${(error as Error).message}`);
        }
    }
}

export const archiveService = new ArchiveService();