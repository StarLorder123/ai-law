from paddleocr import PaddleOCR
import fitz  # PyMuPDF
import numpy as np
from PIL import Image
import os
from pathlib import Path
from typing import List, Optional, Dict, Union

class OcrReader:
    def __init__(self):
        """初始化OCR读取器"""
        # 初始化OCR引擎，支持中文和自动角度检测
        self.ocr = PaddleOCR(use_angle_cls=True, lang="ch")

    def read_pdf(self, pdf_path: str, start_page: Optional[int] = None, end_page: Optional[int] = None) -> str:
        """
        使用OCR读取PDF文件内容
        Args:
            pdf_path: PDF文件路径
            start_page: 开始页码（从1开始），如果不指定则从第一页开始
            end_page: 结束页码（包含），如果不指定则读取到最后一页
        Returns:
            str: 识别出的文本内容
        """
        if not os.path.exists(pdf_path):
            raise FileNotFoundError(f"PDF文件不存在: {pdf_path}")

        try:
            # 打开PDF文件
            pdf_document = fitz.open(pdf_path)
            total_pages = len(pdf_document)

            # 处理页码范围
            if start_page is None:
                start_page = 1
            if end_page is None:
                end_page = total_pages

            # 确保页码在有效范围内
            start_page = max(1, min(start_page, total_pages))
            end_page = max(start_page, min(end_page, total_pages))

            text_content = []
            
            # 遍历指定页码范围
            for page_num in range(start_page - 1, end_page):
                # 获取页面
                page = pdf_document[page_num]
                
                # 将页面转换为图片
                pix = page.get_pixmap(matrix=fitz.Matrix(2, 2))  # 2x缩放以提高清晰度
                img = Image.frombytes("RGB", [pix.width, pix.height], pix.samples)
                
                # 进行OCR识别
                result = self.ocr.ocr(np.array(img))
                
                # 提取识别的文本
                page_text = []
                if result is not None:
                    for line in result[0]:
                        if line[1][0]:  # 确保有识别结果
                            page_text.append(line[1][0])
                
                text_content.append('\n'.join(page_text))

            return '\n\n'.join(text_content)

        except Exception as e:
            raise Exception(f"OCR识别失败: {str(e)}")

    def read_image(self, image_path: str) -> str:
        """
        使用OCR读取图片文件内容
        Args:
            image_path: 图片文件路径
        Returns:
            str: 识别出的文本内容
        """
        if not os.path.exists(image_path):
            raise FileNotFoundError(f"图片文件不存在: {image_path}")

        try:
            # 读取图片并进行OCR识别
            result = self.ocr.ocr(image_path)
            
            # 提取识别的文本
            text_content = []
            if result is not None:
                for line in result[0]:
                    if line[1][0]:  # 确保有识别结果
                        text_content.append(line[1][0])
            
            return '\n'.join(text_content)

        except Exception as e:
            raise Exception(f"OCR识别失败: {str(e)}")

    def save_result(self, text: str, output_path: str) -> str:
        """
        保存OCR识别结果到文件
        Args:
            text: 要保存的文本内容
            output_path: 输出文件路径
        Returns:
            str: 保存的文件路径
        """
        try:
            # 确保输出目录存在
            os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
            
            # 保存文本内容
            with open(output_path, 'w', encoding='utf-8') as f:
                f.write(text)
            
            return output_path
        except Exception as e:
            raise Exception(f"保存结果失败: {str(e)}")

    def get_detailed_result(self, image_path: str) -> List[Dict[str, Union[str, float, List]]]:
        """
        获取详细的OCR识别结果，包括文本位置和置信度
        Args:
            image_path: 图片文件路径
        Returns:
            List[Dict]: 包含详细识别信息的列表
        """
        if not os.path.exists(image_path):
            raise FileNotFoundError(f"文件不存在: {image_path}")

        try:
            result = self.ocr.ocr(image_path)
            detailed_results = []
            
            if result is not None:
                for line in result[0]:
                    if line[1][0]:  # 确保有识别结果
                        detailed_results.append({
                            'text': line[1][0],
                            'confidence': float(line[1][1]),
                            'position': line[0]
                        })
            
            return detailed_results

        except Exception as e:
            raise Exception(f"OCR识别失败: {str(e)}") 