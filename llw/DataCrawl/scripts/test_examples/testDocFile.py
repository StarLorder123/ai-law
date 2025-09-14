from doc_reader import DocReader
import re

def extract_numbered_sections(text):
    # 定义中文数字
    chinese_nums = "一二三四五六七八九十"
    # 匹配完整的条款内容
    pattern = f'([{chinese_nums}]+、[^一二三四五六七八九十]+?)(?=[{chinese_nums}]+、|$)'
    matches = re.findall(pattern, text, re.DOTALL)
    
    # 清理提取的文本
    cleaned_matches = []
    for match in matches:
        cleaned_text = '\n'.join(line.strip() for line in match.split('\n') if line.strip())
        cleaned_matches.append(cleaned_text)
    
    return cleaned_matches

# 示例使用
doc_filepath = "E:\Code\LawProject\DataCrawl\data\张某与钟某等侵害外观设计专利权纠纷二审民事判决书.doc"
doc_reader = DocReader()
text = doc_reader.read_doc(doc_filepath)
numbered_sections = extract_numbered_sections(text)
print("提取的条款内容：")
for i, section in enumerate(numbered_sections, 1):
    print(f"\n=== 第{i}条 ===")
    print(section)
    print("="*50)