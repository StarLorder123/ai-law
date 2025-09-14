import json
from typing import Any, Union, Dict, List
from pathlib import Path
import os

class JsonConverter:
    @staticmethod
    def to_json_string(obj: Any, pretty: bool = False) -> str:
        """
        将Python对象转换为JSON字符串
        Args:
            obj: 要转换的Python对象
            pretty: 是否进行格式化输出，默认为False
        Returns:
            str: JSON字符串
        """
        try:
            if pretty:
                return json.dumps(obj, ensure_ascii=False, indent=4)
            return json.dumps(obj, ensure_ascii=False)
        except Exception as e:
            raise Exception(f"转换JSON字符串失败: {str(e)}")

    @staticmethod
    def from_json_string(json_str: str) -> Any:
        """
        将JSON字符串转换为Python对象
        Args:
            json_str: JSON字符串
        Returns:
            Any: 转换后的Python对象
        """
        try:
            return json.loads(json_str)
        except Exception as e:
            raise Exception(f"解析JSON字符串失败: {str(e)}")

    @staticmethod
    def save_to_file(obj: Any, file_path: str, pretty: bool = True) -> str:
        """
        将Python对象保存为JSON文件
        Args:
            obj: 要保存的Python对象
            file_path: 文件保存路径
            pretty: 是否进行格式化输出，默认为True
        Returns:
            str: 保存的文件路径
        """
        try:
            # 确保输出目录存在
            os.makedirs(os.path.dirname(os.path.abspath(file_path)), exist_ok=True)
            
            with open(file_path, 'w', encoding='utf-8') as f:
                if pretty:
                    json.dump(obj, f, ensure_ascii=False, indent=4)
                else:
                    json.dump(obj, f, ensure_ascii=False)
            return file_path
        except Exception as e:
            raise Exception(f"保存JSON文件失败: {str(e)}")

    @staticmethod
    def load_from_file(file_path: str) -> Any:
        """
        从JSON文件加载Python对象
        Args:
            file_path: JSON文件路径
        Returns:
            Any: 加载的Python对象
        """
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"文件不存在: {file_path}")
            
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            raise Exception(f"加载JSON文件失败: {str(e)}")

    @staticmethod
    def merge_json_objects(*objects: Dict) -> Dict:
        """
        合并多个JSON对象
        Args:
            *objects: 要合并的JSON对象
        Returns:
            Dict: 合并后的JSON对象
        """
        result = {}
        for obj in objects:
            result.update(obj)
        return result

    @staticmethod
    def validate_json_string(json_str: str) -> bool:
        """
        验证JSON字符串是否有效
        Args:
            json_str: 要验证的JSON字符串
        Returns:
            bool: 是否为有效的JSON字符串
        """
        try:
            json.loads(json_str)
            return True
        except:
            return False 