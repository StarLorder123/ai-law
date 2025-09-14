// 生成随机32位字符串（类似 ee0df0903d5d4d44ba6600079a0d072b）
export const generateRandom32BitNumber = () => {
    // 生成32位随机字符串
    return Array.from(
        { length: 32 },
        () => Math.floor(Math.random() * 16).toString(16)
    ).join('');
};

// 生成带分隔符的随机字符串（类似 ee0df090-3d5d-4d44-ba66-00079a0d072b）
export const generateUUID = () => {
    const segments = [8, 4, 4, 4, 12]; // UUID 格式的每段长度
    let uuid = '';

    segments.forEach((length, index) => {
        uuid += Array.from(
            { length },
            () => Math.floor(Math.random() * 16).toString(16)
        ).join('');

        if (index < segments.length - 1) {
            uuid += '-';
        }
    });

    return uuid;
};

// 生成指定长度的随机字符串
export const generateRandomString = (length = 32, options = {}) => {
    const {
        numbers = true,     // 是否包含数字
        lowercase = true,   // 是否包含小写字母
        uppercase = false,  // 是否包含大写字母
        special = false,    // 是否包含特殊字符
    } = options;

    let chars = '';
    if (numbers) chars += '0123456789';
    if (lowercase) chars += 'abcdefghijklmnopqrstuvwxyz';
    if (uppercase) chars += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    if (special) chars += '!@#$%^&*()_+-=[]{}|;:,.<>?';

    return Array.from(
        { length },
        () => chars[Math.floor(Math.random() * chars.length)]
    ).join('');
};