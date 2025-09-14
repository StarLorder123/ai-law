import re
from doc_reader import DocReader

def extract_numbered_sections(text):
    """
    提取文本中"中文数字、"开头的条款内容
    Args:
        text: 输入的文本内容
    Returns:
        list: 包含所有匹配内容的列表
    """
    # 定义中文数字
    chinese_nums = "一二三四五六七八九十"
    
    # 按回车切分文本
    lines = text.split('\n')
    result = []
    current_section = None
    
    def has_section_header(line):
        """检查一行文本前三个字是否包含"中文数字、"的格式"""
        if len(line) < 3:
            return False
        first_three = line[:3]
        for num in chinese_nums:
            if f"{num}、" in first_three:
                return True
        return False
    
    # 遍历每一行
    for line in lines:
        line = line.strip()
        if not line:  # 跳过空行
            continue
            
        if has_section_header(line):
            # 如果有之前的section，先保存
            if current_section is not None:
                result.append(current_section)
            # 开始新的section
            current_section = line
        elif current_section is not None:
            # 如果当前行不是新section的开始，且有正在处理的section，则附加到当前section
            current_section = current_section + '\n' + line
    
    # 添加最后一个section
    if current_section is not None:
        result.append(current_section)
    
    return result

def extract_article_sections(text):
    """
    提取文本中"第x条"开头的条款内容
    Args:
        text: 输入的文本内容
    Returns:
        list: 包含所有匹配内容的列表
    """
    # 按回车切分文本
    lines = text.split('\n')
    result = []
    current_section = None
    
    def has_section_header(line):
        """检查一行文本是否以'第x条'开头"""
        import re
        # 匹配"第"开头，后面跟数字，再跟"条"的模式
        pattern = r'^第[一二三四五六七八九十百千]+条'
        return bool(re.match(pattern, line.strip()))
    
    # 遍历每一行
    for line in lines:
        line = line.strip()
        if not line:  # 跳过空行
            continue
            
        if has_section_header(line):
            # 如果有之前的section，先保存
            if current_section is not None:
                result.append(current_section)
            # 开始新的section
            current_section = line
        elif current_section is not None:
            # 如果当前行不是新section的开始，且有正在处理的section，则附加到当前section
            current_section = current_section + '\n' + line
    
    # 添加最后一个section
    if current_section is not None:
        result.append(current_section)
    
    return result

def extract_explanation_sections(text):
    """
    提取文本中"xxxx的解释xx"格式的标题、其前面所有内容及后续内容
    Args:
        text: 输入的文本内容
    Returns:
        list: 包含所有匹配内容的列表，每个元素是一个字典，包含标题和内容
    """
    # 按回车切分文本
    lines = text.split('\n')
    result = []
    
    def is_explanation_title(line):
        """检查一行文本是否符合"xxxx的解释xx"格式"""
        import re
        pattern = r'.*的解释.*'
        return bool(re.match(pattern, line.strip()))
    
    # 遍历每一行
    for i in range(len(lines)):
        line = lines[i].strip()
        if not line:  # 跳过空行
            continue
            
        if is_explanation_title(line):
            # 获取前面的所有内容
            previous_lines = []
            for j in range(0, i):  # 从开始到当前行
                prev_line = lines[j].strip()
                if prev_line:  # 只添加非空行
                    previous_lines.append(prev_line)
            
            # 将前面的内容和当前行合并成一个字符串
            title_with_context = '\n'.join(previous_lines + [line])
            
            # 获取后续两行内容（如果存在）
            next_lines = []
            for j in range(1, 3):  # 获取后两行
                if i + j < len(lines):
                    next_line = lines[i + j].strip()
                    if next_line:  # 只添加非空行
                        next_lines.append(next_line)
            
            # 将结果添加到列表中
            result.append({
                'title': title_with_context,
                'content': next_lines
            })
    
    return result

# 使用示例
if __name__ == "__main__":
    # docx_path = "E:\\Code\\llw\\DataCrawl\\data\\中华人民共和国刑法修正案（十二）.docx"
    docx_path = "E:\\Code\\llw\DataCrawl\\data\\最高人民法院 最高人民检察院关于办理侵犯知识产权刑事案件具体应用法律若干问题的解释（三）.docx"
    doc_reader = DocReader()
    text_content = doc_reader.read_docx(docx_path)
    print("读取的内容为：",text_content)
    # 提取编号段落
    # numbered_sections = extract_article_sections(text_content)
    # print("提取的条款内容：")
    # for i, section in enumerate(numbered_sections, 1):
    #     print(f"\n=== 第{i}条 ===")
    #     print(section)
    #     print("="*50)

    result=extract_explanation_sections(text_content)
    print("extract_explanation_sections提取的内容为：",result)
