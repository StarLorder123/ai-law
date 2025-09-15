from spire.doc import *
from spire.doc.common import *
import re


def remove_spire_watermark(html_content):
    """移除Spire.Doc生成的水印"""
    try:
        # 移除包含水印的span标签
        watermark_pattern = r'<span style=".*?">Evaluation Warning: The document was created with Spire\.Doc for Python\.</span>'
        cleaned_content = re.sub(watermark_pattern, '', html_content)

        # 移除可能的水印div容器
        watermark_div_pattern = r'<div[^>]*?>\s*Evaluation Warning: The document was created with Spire\.Doc for Python\.\s*</div>'
        cleaned_content = re.sub(watermark_div_pattern, '', cleaned_content)

        # 移除可能的水印相关样式
        style_pattern = r'<style>.*?\.evaluation-warning.*?</style>'
        cleaned_content = re.sub(style_pattern, '', cleaned_content, flags=re.DOTALL)

        return cleaned_content
    except Exception as e:
        print(f"移除水印时出错：{str(e)}")
        return html_content


def process_single_file(input_path, html_path):
    """处理单个文件"""
    if not input_path.endswith('.docx'):
        print(f"错误：文件 '{input_path}' 不是docx格式")
        return None

    convert_flag = convert_docx_to_html(input_path, html_path)
    if convert_flag:
        try:
            # 读取生成的HTML文件
            with open(html_path, 'r', encoding='utf-8') as f:
                content = f.read()

            # 移除水印
            cleaned_content = remove_spire_watermark(content)

            # 写回文件
            with open(html_path, 'w', encoding='utf-8') as f:
                f.write(cleaned_content)

        except Exception as e:
            print(f"处理水印时出错：{str(e)}")

    return html_path

def convert_docx_to_html(docx_path, html_path=None):
    """
    将DOCX文件转换为HTML格式

    参数:
        docx_path (str): DOCX文件的路径
        html_path (str, optional): 输出HTML文件的路径。如果未指定，将使用与DOCX相同的名称

    返回:
        bool: 转换成功返回True，失败返回False
    """
    try:
        # 如果未指定html路径，则使用docx文件名（替换扩展名）
        if html_path is None:
            html_path = os.path.splitext(docx_path)[0] + '.html'

        # 创建一个 Document 对象
        document = Document()
        # 加载一个 Word DOCX 文档
        document.LoadFromFile(docx_path)

        # 设置是否在 HTML 中嵌入图片
        document.HtmlExportOptions.ImageEmbedded = True

        # 设置是否将表单字段导出为纯文本在 HTML 中显示
        document.HtmlExportOptions.IsTextInputFormFieldAsText = True

        # 设置在HTML中嵌入css
        document.HtmlExportOptions.CssStyleSheetType = CssStyleSheetType.Internal
        # 设置是否在 HTML 中导出页眉和页脚
        document.HtmlExportOptions.HasHeadersFooters = False

        # 将 Word 文档保存为 HTML 文件
        document.SaveToFile(html_path, FileFormat.Html)

        document.Close()


        return True

    except Exception as e:
        print(f"转换过程中出现错误: {str(e)}")
        return False


def batch_convert_docx_to_html(folder_path,out_path):
    """
    批量转换文件夹中的所有DOCX文件为HTML格式

    参数:
        folder_path (str): 包含DOCX文件的文件夹路径
        out_path (str): 输出HTML文件的路径
    """
    if not os.path.exists(out_path):
        os.makedirs(out_path)

    converted_files = []

    for filename in os.listdir(folder_path):
        if filename.endswith('.docx'):
            docx_path = os.path.join(folder_path, filename)
            try:
                html_path = os.path.join(out_path, os.path.splitext(filename)[0] + '.html')
                success = process_single_file(docx_path, html_path)
                if success:
                    converted_files.append(html_path)
                    print(f"成功转换: {filename}")
                else:
                    print(f"转换 {filename} 时出错")
            except Exception as e:
                print(f"转换 {filename} 时出错: {str(e)}")

    return converted_files





if __name__ == "__main__":

    # 批量转换
    docx_folder = "C:\\Users\\Admin\\Desktop\\民事判决书"
    html_folder = "C:\\Users\\Admin\\Desktop\\文书转html"
    if os.path.exists(docx_folder):
        converted_files = batch_convert_docx_to_html(docx_folder,html_folder)
        print(f"成功转换 {len(converted_files)} 个文件")
    else:
        print(f"文件夹 {docx_folder} 不存在")