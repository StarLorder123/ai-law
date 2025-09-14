import os
from pathlib import Path
from typing import List, Optional, Callable

class FolderTraversal:
    def __init__(self, root_path: str):
        """
        初始化文件夹遍历器
        Args:
            root_path: 根目录路径
        """
        self.root_path = Path(root_path)
        
    def traverse(self, 
                file_types: Optional[List[str]] = None, 
                exclude_dirs: Optional[List[str]] = None,
                file_filter: Optional[Callable[[str], bool]] = None) -> List[str]:
        """
        遍历文件夹
        Args:
            file_types: 要查找的文件类型列表，例如 ['.docx', '.doc']
            exclude_dirs: 要排除的目录名列表
            file_filter: 自定义文件过滤函数
        Returns:
            List[str]: 符合条件的文件路径列表
        """
        if not self.root_path.exists():
            raise FileNotFoundError(f"路径不存在: {self.root_path}")
            
        if exclude_dirs is None:
            exclude_dirs = []
            
        result = []
        
        for root, dirs, files in os.walk(self.root_path):
            # 排除指定目录
            dirs[:] = [d for d in dirs if d not in exclude_dirs]
            
            for file in files:
                file_path = Path(root) / file
                
                # 检查文件类型
                if file_types and not any(file.lower().endswith(ft.lower()) for ft in file_types):
                    continue
                    
                # 应用自定义过滤器
                if file_filter and not file_filter(str(file_path)):
                    continue
                    
                result.append(str(file_path))
                
        return result
    
    def traverse_by_name(self, name_pattern: str) -> List[str]:
        """
        按文件名模式遍历文件夹
        Args:
            name_pattern: 文件名模式（支持通配符），例如 "*.docx"
        Returns:
            List[str]: 符合条件的文件路径列表
        """
        def file_filter(file_path: str) -> bool:
            from fnmatch import fnmatch
            return fnmatch(Path(file_path).name, name_pattern)
            
        return self.traverse(file_filter=file_filter)
    
    def get_doc_files(self, exclude_dirs: Optional[List[str]] = None) -> List[str]:
        """
        获取所有Word文档文件
        Args:
            exclude_dirs: 要排除的目录名列表
        Returns:
            List[str]: Word文档文件路径列表
        """
        return self.traverse(
            file_types=['.doc', '.docx'],
            exclude_dirs=exclude_dirs
        ) 