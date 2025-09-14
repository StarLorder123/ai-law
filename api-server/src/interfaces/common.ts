import { ObjectId } from 'mongodb';

// 定义模板项目接口
export interface TemplateItem {
    index: number;
    title: string;
    required: boolean;
    category: string;
    description: string;
}

// 定义基础模板接口
export interface BaseTemplate {
    name: string;
    description: string;
    version: number;
    created_by: string;
    created_at: Date;
    items: Record<string, TemplateItem>;
}

/**
 * 定义模板接口（用于模板集合）
 * @example
 * {
 *   "_id": "507f1f77bcf86cd799439013",
 *   "name": "民事案件通用模板",
 *   "description": "适用于一般民事案件的电子卷宗结构",
 *   "version": 1,
 *   "created_by": "admin",
 *   "created_at": "2025-04-10T08:30:00.000Z",
 *   "items": {
 *     "item1": {
 *       "index": 1,
 *       "title": "起诉状",
 *       "required": true,
 *       "category": "文书",
 *       "description": "原告提交的起诉文件"
 *     },
 *     "item2": {
 *       "index": 2,
 *       "title": "证据材料",
 *       "required": false,
 *       "category": "证据",
 *       "description": "原告提交的证据材料"
 *     },
 *     "item3": {
 *       "index": 3,
 *       "title": "身份证明",
 *       "required": true,
 *       "category": "证明文件",
 *       "description": "原告的身份证明文件"
 *     }
 *   }
 * }
 */
export interface Template extends BaseTemplate {
    _id?: ObjectId | string;
}

// 定义卷宗模板接口（用于卷宗实例中的模板）
export interface CatalogTemplate extends BaseTemplate {
    id: string;
}

// 定义卷宗文件项接口
/**
 * 定义卷宗文件项接口
 * @example
 * {
 *   "file_id": "507f1f77bcf86cd799439011",
 *   "item_key": "item1",
 *   "upload_time": "2025-04-11T08:30:00.000Z",
 *   "status": "pending",
 *   "remarks": "等待审核中的起诉状文件"
 * }
 */
export interface CatalogFileItem {
    file_id: string;          // 关联的文件ID
    item_key: string;         // 对应模板中的item key，如 "item1"
    upload_time: Date;        // 文件上传时间
    status: 'pending' | 'approved' | 'rejected';  // 文件状态
    remarks?: string;         // 备注信息
}

/**
 * 更新卷宗实例接口
 * @example
 * {
 *   "_id": "507f1f77bcf86cd799439012",
 *   "case_id": "CASE-2025-001",
 *   "template_id": "TPL-CIVIL-001",
 *   "generated_at": "2025-04-11T08:00:00.000Z",
 *   "template": {
 *     "id": "TPL-CIVIL-001",
 *     "name": "民事案件通用模板",
 *     "description": "适用于一般民事案件的电子卷宗结构",
 *     "version": 1,
 *     "created_by": "admin",
 *     "created_at": "2025-04-10T08:30:00.000Z",
 *     "items": {
 *       "item1": {
 *         "index": 1,
 *         "title": "起诉状",
 *         "required": true,
 *         "category": "文书",
 *         "description": "原告提交的起诉文件"
 *       }
 *     }
 *   },
 *   "files": [
 *     {
 *       "file_id": "507f1f77bcf86cd799439011",
 *       "item_key": "item1",
 *       "upload_time": "2025-04-11T08:30:00.000Z",
 *       "status": "pending",
 *       "remarks": "等待审核中的起诉状文件"
 *     }
 *   ],
 *   "status": "draft",
 *   "last_modified": "2025-04-11T08:30:00.000Z"
 * }
 */
export interface Catalog {
    _id?: ObjectId | string;
    case_id: string;
    template_id: string;
    generated_at: Date;
    template: CatalogTemplate;
    files: CatalogFileItem[];     // 添加文件关系数组
    status: 'draft' | 'submitted' | 'approved' | 'rejected';  // 卷宗状态
    last_modified: Date;          // 最后修改时间
}

// 定义文件元数据接口
/**
 * 定义文件元数据接口
 * @example
 * {
 *   "_id": "507f1f77bcf86cd799439014",
 *   "file_name": "f27b8a2c3d1f4a92a038d4ea1a1c2d34.pdf",
 *   "type": "pdf",
 *   "size": 1024576,
 *   "create_time": "2025-04-11T08:30:00.000Z"
 * }
 */
export interface FileMetadata {
    _id?: ObjectId;
    file_name: string;
    type: string;
    size: number;
    create_time: Date;
    storage_path: string; // 文件存储的相对路径
}