from doc_reader import PdfConverter
import os
import time
from pathlib import Path

def test_single_pdf_conversion():
    """测试单个PDF文件转换功能"""
    # 设置PDF文件路径和输出目录
    pdf_path = "D:\\BaiduNetdiskDownload\\法律条文\\国家级文书\\中国公民出国旅游管理办法.pdf"  # 替换为你的PDF文件路径
    output_dir = os.path.join(os.path.dirname(pdf_path), "converted_docs")  # 输出到同一目录下的converted_docs文件夹
    
    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)
    
    # 设置输出文件路径
    pdf_name = Path(pdf_path).stem
    output_path = os.path.join(output_dir, f"{pdf_name}.docx")
    
    print(f"=== 开始转换PDF文件 ===")
    print(f"源文件: {pdf_path}")
    print(f"目标文件: {output_path}")
    
    # 记录开始时间
    start_time = time.time()
    
    try:
        # 转换文件
        docx_path = PdfConverter.pdf_to_docx(pdf_path, output_path)
        
        # 检查转换结果
        if os.path.exists(docx_path):
            # 计算转换用时和文件大小
            duration = time.time() - start_time
            pdf_size_mb = os.path.getsize(pdf_path) / (1024 * 1024)
            docx_size_mb = os.path.getsize(docx_path) / (1024 * 1024)
            
            # 打印转换结果
            print("\n=== 转换成功 ===")
            print(f"转换后文件: {docx_path}")
            print(f"转换耗时: {duration:.2f}秒")
            print(f"PDF文件大小: {pdf_size_mb:.2f}MB")
            print(f"DOCX文件大小: {docx_size_mb:.2f}MB")
        else:
            print("\n=== 转换失败 ===")
            print("输出文件不存在")
            
    except FileNotFoundError as e:
        print("\n=== 转换失败 ===")
        print(f"错误: {str(e)}")
    except Exception as e:
        print("\n=== 转换失败 ===")
        print(f"错误: {str(e)}")

if __name__ == "__main__":
    try:
        test_single_pdf_conversion()
    except Exception as e:
        print(f"测试过程中发生错误: {str(e)}") 