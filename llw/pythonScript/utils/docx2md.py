import os
import re
import mammoth
from docx import Document
from docx.enum.text import WD_PARAGRAPH_ALIGNMENT
from docx.shared import Pt



class DocxToMarkdown:
    def __init__(self):
        self.heading_sizes = {
            Pt(24): 1,  # 24磅字体对应h1
            Pt(18): 2,  # 18磅字体对应h2
            Pt(16): 3,  # 16磅字体对应h3
            Pt(14): 4   # 14磅字体对应h4
        }

    def convert_to_markdown(self, docx_path, output_folder):
        """将docx文件转换为markdown格式"""
        base_name = os.path.splitext(os.path.basename(docx_path))[0]
        output_path = os.path.join(output_folder, f"{base_name}.md")
        
        # 使用python-docx读取格式信息
        doc = Document(docx_path)
        format_info = self.extract_format_info(doc)
        
        # 使用mammoth进行基础转换
        with open(docx_path, 'rb') as docx_file:
            result = mammoth.convert_to_markdown(docx_file)
            markdown_content = result.value
        
        # 后处理markdown内容，应用格式信息
        processed_content = self.post_process_markdown(markdown_content, format_info)
        
        # 写入文件
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(processed_content)
        
        return output_path

    def extract_format_info(self, doc):
        """提取文档中的格式信息"""
        format_info = {}
        
        for i, para in enumerate(doc.paragraphs):
            if not para.text.strip():
                continue
            
            # 获取段落中最大的字体大小
            max_font_size = max((run.font.size for run in para.runs if run.font.size), default=None)
            
            # 收集段落中所有运行的格式信息
            runs_info = []
            for run in para.runs:
                run_info = {
                    'text': run.text,
                    'bold': run.bold,
                    'italic': run.italic,
                    'underline': run.underline,
                    'font_size': run.font.size
                }
                runs_info.append(run_info)
            
            para_info = {
                'text': para.text.strip(),
                'alignment': para.alignment,
                'font_size': max_font_size,
                'first_line_indent': para.paragraph_format.first_line_indent,
                'runs': runs_info,
                'style': para.style.name if para.style else None
            }
            format_info[para.text.strip()] = para_info
            
        return format_info

    def post_process_markdown(self, content, format_info):
        """对转换后的markdown内容进行后处理"""
        lines = content.split('\n')
        processed_lines = []
        
        for line in lines:
            original_line = line.strip()
            if not original_line:
                continue
            
            # 获取该行文本对应的格式信息
            info = format_info.get(original_line, {})
            
            # 处理标题（基于字体大小）
            if info.get('font_size'):
                for size, level in self.heading_sizes.items():
                    if info['font_size'] >= size:
                        line = f"{'#' * level} {original_line}"
                        break
            
            # 处理对齐方式
            if info.get('alignment') == WD_PARAGRAPH_ALIGNMENT.CENTER:
                line = f"<div align=\"center\">{line}</div>"
            elif info.get('alignment') == WD_PARAGRAPH_ALIGNMENT.RIGHT:
                line = f"<div align=\"right\">{line}</div>"
            
            # 处理首行缩进
            if info.get('first_line_indent'):
                line = f"&emsp;&emsp;{line}"
            
            # 处理行内格式
            if info.get('runs'):
                result = []
                for run in info['runs']:
                    text = run['text']
                    if run.get('bold'):
                        text = f"**{text}**"
                    if run.get('italic'):
                        text = f"*{text}*"
                    if run.get('underline'):
                        text = f"<u>{text}</u>"
                    result.append(text)
                line = ''.join(result)
            
            processed_lines.append(line)
        
        return '\n'.join(processed_lines)

def batch_convert_to_markdown(input_folder, output_folder):
    """批量转换文件夹中的所有docx文件为markdown"""
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
    
    converter = DocxToMarkdown()
    converted_files = []
    
    for filename in os.listdir(input_folder):
        if filename.endswith('.docx'):
            input_path = os.path.join(input_folder, filename)
            try:
                output_path = converter.convert_to_markdown(input_path, output_folder)
                converted_files.append(output_path)
                print(f"成功转换: {filename}")
            except Exception as e:
                print(f"转换 {filename} 时出错: {str(e)}")
    
    return converted_files

if __name__ == "__main__":

    
    input_folder = "C:\\Users\\Admin\\Desktop\\民事判决书"
    output_folder = "C:\\Users\\Admin\\Desktop\\文书转md"
    
    print("开始转换文件为Markdown格式...")
    converted_files = batch_convert_to_markdown(input_folder, output_folder)
    print(f"转换完成，共转换 {len(converted_files)} 个文件")