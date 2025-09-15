import re


def remove_brackets_content(text):
    """
    移除字符串中所有中文【】和英文()括号及其包含的内容

    参数：
    text (str): 原始文本字符串

    返回：
    str: 清理后的文本

    示例：
    >>> remove_brackets_content("这是【测试】内容（示例）")
    '这是内容'
    >>> remove_brackets_content("Hello(World)【你好】！")
    'Hello！'
    """
    # 匹配中文括号【】及其内容
    pattern_cn = r'【[^】]*】'
    # 匹配英文括号()及其内容
    pattern_en = r'（[^）]*）'

    # 组合正则表达式，同时移除两种括号
    combined_pattern = f'{pattern_cn}|{pattern_en}'

    # 执行替换操作
    cleaned_text = re.sub(combined_pattern, '', text)

    return cleaned_text


if __name__ == '__main__':
    str=remove_brackets_content("这是【测试】内容（示例）")
    print(str)