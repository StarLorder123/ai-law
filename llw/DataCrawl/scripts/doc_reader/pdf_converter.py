from pdf2docx import Converter
import os
from pathlib import Path
import PyPDF2
from typing import List, Optional
from paddleocr import PaddleOCR
import fitz  # PyMuPDF
import numpy as np
from PIL import Image
import io

class PdfConverter:
    @staticmethod
    def pdf_to_docx(pdf_path: str, output_path: str = None) -> str:
        """
        将PDF文件转换为DOCX文件
        Args:
            pdf_path: PDF文件路径
            output_path: 输出的DOCX文件路径，如果不指定则使用相同文件名
        Returns:
            str: 转换后的DOCX文件路径
        """
        if not os.path.exists(pdf_path):
            raise FileNotFoundError(f"PDF文件不存在: {pdf_path}")
            
        # 如果未指定输出路径，则使用相同的文件名
        if output_path is None:
            output_path = str(Path(pdf_path).with_suffix('.docx'))
            
        # 确保输出目录存在
        os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
        
        try:
            # 创建转换器
            cv = Converter(pdf_path)
            # 转换PDF到DOCX
            cv.convert(output_path)
            # 关闭转换器
            cv.close()
            
            return output_path
        except Exception as e:
            raise Exception(f"PDF转换失败: {str(e)}")

    @staticmethod
    def read_pdf(pdf_path: str, start_page: Optional[int] = None, end_page: Optional[int] = None) -> str:
        """
        读取PDF文件内容
        Args:
            pdf_path: PDF文件路径
            start_page: 开始页码（从1开始），如果不指定则从第一页开始
            end_page: 结束页码（包含），如果不指定则读取到最后一页
        Returns:
            str: PDF文件的文本内容
        """
        if not os.path.exists(pdf_path):
            raise FileNotFoundError(f"PDF文件不存在: {pdf_path}")

        try:
            with open(pdf_path, 'rb') as file:
                # 创建PDF读取器
                pdf_reader = PyPDF2.PdfReader(file)
                total_pages = len(pdf_reader.pages)

                # 处理页码范围
                if start_page is None:
                    start_page = 1
                if end_page is None:
                    end_page = total_pages

                # 确保页码在有效范围内
                start_page = max(1, min(start_page, total_pages))
                end_page = max(start_page, min(end_page, total_pages))

                # 读取指定页码范围的内容
                text_content = []
                for page_num in range(start_page - 1, end_page):
                    page = pdf_reader.pages[page_num]
                    text_content.append(page.extract_text())

                return '\n'.join(text_content)

        except Exception as e:
            raise Exception(f"PDF读取失败: {str(e)}")

    @staticmethod
    def get_pdf_info(pdf_path: str) -> dict:
        """
        获取PDF文件的基本信息
        Args:
            pdf_path: PDF文件路径
        Returns:
            dict: PDF文件的基本信息，包括页数、元数据等
        """
        if not os.path.exists(pdf_path):
            raise FileNotFoundError(f"PDF文件不存在: {pdf_path}")

        try:
            with open(pdf_path, 'rb') as file:
                pdf_reader = PyPDF2.PdfReader(file)
                
                info = {
                    'total_pages': len(pdf_reader.pages),
                    'file_size_mb': os.path.getsize(pdf_path) / (1024 * 1024),
                    'metadata': pdf_reader.metadata
                }
                
                return info

        except Exception as e:
            raise Exception(f"获取PDF信息失败: {str(e)}") 

    @staticmethod
    def extract_text_with_ocr(pdf_path: str, start_page: Optional[int] = None, end_page: Optional[int] = None) -> str:
        """
        使用OCR提取PDF文件中的文本（适用于扫描版PDF）
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
            # 初始化OCR引擎
            ocr = PaddleOCR(use_angle_cls=True, lang="ch")  # 支持中文和自动角度检测
            
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
                result = ocr.ocr(np.array(img))
                
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