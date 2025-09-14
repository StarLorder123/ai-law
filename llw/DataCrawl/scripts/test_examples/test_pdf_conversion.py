from doc_reader import PdfConverter
from file_traversal import FolderTraversal
import os
import time

def test_pdf_conversion():
    """测试PDF转DOCX功能"""
    # 设置测试目录
    base_path = "D:\\BaiduNetdiskDownload\\法律条文\\国家级文书"  # 替换为你的实际路径
    output_dir = os.path.join(base_path, "converted_docs")
    
    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)
    
    # 使用文件遍历器找到所有PDF文件
    traversal = FolderTraversal(base_path)
    pdf_files = traversal.traverse(file_types=['.pdf'])
    
    if not pdf_files:
        print("未找到PDF文件")
        return
        
    print(f"找到 {len(pdf_files)} 个PDF文件")
    
    # 测试单个文件转换
    def convert_single_file(pdf_path):
        print(f"\n正在转换: {pdf_path}")
        start_time = time.time()
        
        try:
            # 在output_dir中创建相对路径结构
            rel_path = os.path.relpath(pdf_path, base_path)
            output_path = os.path.join(output_dir, os.path.splitext(rel_path)[0] + '.docx')
            
            # 转换文件
            docx_path = PdfConverter.pdf_to_docx(pdf_path, output_path)
            
            # 检查转换结果
            if os.path.exists(docx_path):
                duration = time.time() - start_time
                size_mb = os.path.getsize(docx_path) / (1024 * 1024)
                print(f"转换成功: {docx_path}")
                print(f"耗时: {duration:.2f}秒")
                print(f"文件大小: {size_mb:.2f}MB")
                return True
            else:
                print(f"转换失败: 输出文件不存在")
                return False
                
        except Exception as e:
            print(f"转换出错: {str(e)}")
            return False
    
    # 批量转换文件
    print("\n=== 开始批量转换 ===")
    success_count = 0
    total_start_time = time.time()
    
    for pdf_file in pdf_files:
        if convert_single_file(pdf_file):
            success_count += 1
            
    total_duration = time.time() - total_start_time
    
    # 打印统计信息
    print("\n=== 转换完成 ===")
    print(f"总文件数: {len(pdf_files)}")
    print(f"成功转换: {success_count}")
    print(f"失败数量: {len(pdf_files) - success_count}")
    print(f"总耗时: {total_duration:.2f}秒")
    print(f"平均每个文件耗时: {total_duration/len(pdf_files):.2f}秒")

if __name__ == "__main__":
    try:
        test_pdf_conversion()
    except Exception as e:
        print(f"测试过程中发生错误: {str(e)}") 