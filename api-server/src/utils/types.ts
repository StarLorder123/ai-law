// 文件类型枚举
export enum FileType {
    IMAGE = 'image',
    VIDEO = 'video',
    AUDIO = 'audio',
    DOCUMENT = 'document',
    OTHER = 'other'
}

// MIME类型映射
export const MimeTypeMap = {
    // 图片
    'image/jpeg': FileType.IMAGE,
    'image/png': FileType.IMAGE,
    'image/gif': FileType.IMAGE,
    'image/webp': FileType.IMAGE,

    // 视频
    'video/mp4': FileType.VIDEO,
    'video/webm': FileType.VIDEO,
    'video/ogg': FileType.VIDEO,

    // 音频
    'audio/mpeg': FileType.AUDIO,
    'audio/wav': FileType.AUDIO,
    'audio/ogg': FileType.AUDIO,

    // 文档
    'application/pdf': FileType.DOCUMENT,
    'application/msword': FileType.DOCUMENT,
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': FileType.DOCUMENT
};

// 文件信息接口
export interface FileInfo {
    id: string;                 // 文件唯一标识符
    originalName: string;       // 原始文件名
    fileName: string;          // 存储的文件名
    fileType: FileType;        // 文件类型
    mimeType: string;          // MIME类型
    size: number;              // 文件大小（字节）
    path: string;              // 存储路径
    url: string;               // 访问URL
    uploadTime: Date;          // 上传时间
    metadata?: {               // 可选的元数据
        width?: number;          // 图片/视频宽度
        height?: number;         // 图片/视频高度
        duration?: number;       // 音频/视频时长
        format?: string;         // 文件格式
        encoding?: string;       // 编码格式
    };
    tags?: string[];          // 可选的标签
    status: 'active' | 'deleted' | 'processing'; // 文件状态
}

// 文件上传响应接口
export interface FileUploadResponse {
    success: boolean;
    data?: FileInfo;
    error?: string;
}

// 文件上传配置接口
export interface FileUploadConfig {
    maxFileSize: number;       // 最大文件大小
    allowedTypes: string[];    // 允许的文件类型
    storageDir: string;        // 存储目录
    generateThumbnail?: boolean; // 是否生成缩略图
    processMetadata?: boolean;   // 是否处理元数据
}