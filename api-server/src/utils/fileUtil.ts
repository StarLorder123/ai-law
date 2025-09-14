import crypto from 'crypto';
import path from 'path';
import fs from 'fs';

import { STORAGE_ROOT_DIR } from '../config/app';

/**
 * 生成一个安全的文件名，包含原始扩展名
 * @param originalName 原始文件名，如 "dog.png"
 * @returns 生成后的安全文件名，如 "f27b8a2c3d1f4a92a038d4ea1a1c2d34.png"
 */
export function generateSafeFileName(originalName: string): string {
    const ext = path.extname(originalName); // .png
    const randomHash = crypto.randomBytes(16).toString('hex'); // 32位安全哈希
    return `${randomHash}${ext}`;
}

// 确保存储目录存在
async function ensureStorageDir() {
    try {
        fs.mkdirSync(STORAGE_ROOT_DIR, { recursive: true });
    } catch (err) {
        console.error('Error creating storage directory:', err);
    }
}

// 写入文件
export async function saveFile(filename: string, content: string | Buffer) {
    await ensureStorageDir();
    const filepath = path.join(STORAGE_ROOT_DIR, filename);
    fs.writeFileSync(filepath, content);
    console.log(`File saved to ${filepath}`);
}

// 读取文件
export async function readFile(filename: string): Promise<string> {
    const filepath = path.join(STORAGE_ROOT_DIR, filename);
    const data = fs.readFileSync(filepath, 'utf-8');
    return data;
}

// 判断文件是否存在
export async function fileExists(filename: string): Promise<boolean> {
    const filepath = path.join(STORAGE_ROOT_DIR, filename);
    try {
        fs.accessSync(filepath);
        return true;
    } catch {
        return false;
    }
}

// 删除文件
export async function deleteFile(filename: string) {
    const filepath = path.join(STORAGE_ROOT_DIR, filename);
    fs.unlinkSync(filepath);
    console.log(`File deleted: ${filepath}`);
}