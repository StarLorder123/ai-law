from doc_reader import OcrReader
import os
from pathlib import Path
import time

def test_ocr_reader():
    """测试OCR读取功能"""
    # 创建OCR读取器实例
    ocr_reader = OcrReader()
    
    # 设置测试文件路径
    pdf_path = "D:\\BaiduNetdiskDownload\\法律条文\\国家级文书\\中国公民出国旅游管理办法.pdf"
    
    print("=== OCR读取测试 ===")
    print(f"测试文件: {pdf_path}")
    
    try:
        # 1. 测试PDF文件OCR
        print("\n1. PDF文件OCR测试")
        start_time = time.time()
        
        # 读取第一页内容
        content = ocr_reader.read_pdf(pdf_path, start_page=1, end_page=1)
        
        duration = time.time() - start_time
        print(f"耗时: {duration:.2f}秒")
        print(f"识别文本长度: {len(content)} 字符")
        print("\n识别结果预览:")
        print(content[:500])
        
        # 2. 保存识别结果
        print("\n2. 保存识别结果测试")
        output_file = os.path.join(os.path.dirname(pdf_path), 
                                 Path(pdf_path).stem + "_ocr_result.txt")
        ocr_reader.save_result(content, output_file)
        print(f"结果已保存至: {output_file}")
        
        # 3. 如果有图片文件，也可以测试图片OCR
        # image_path = "path/to/your/image.jpg"
        # if os.path.exists(image_path):
        #     print("\n3. 图片OCR测试")
        #     image_content = ocr_reader.read_image(image_path)
        #     print(f"识别文本长度: {len(image_content)} 字符")
        #     print("\n图片识别结果预览:")
        #     print(image_content[:500])
        
    except Exception as e:
        print(f"\n=== 测试失败 ===")
        print(f"错误: {str(e)}")

if __name__ == "__main__":
    try:
        test_ocr_reader()
    except Exception as e:
        print(f"测试过程中发生错误: {str(e)}") 