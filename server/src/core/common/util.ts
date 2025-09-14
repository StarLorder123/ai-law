/**
 * 生成指定长度的UUID
 * @param length UUID长度，默认32位
 * @returns 指定长度的UUID字符串
 */
export function generateUUID(length: number = 32): string {
    const chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    const uuid = [];
    const radix = chars.length;

    for (let i = 0; i < length; i++) {
        uuid[i] = chars[Math.floor(Math.random() * radix)];
    }

    return uuid.join('');
}