from file_traversal import FolderTraversal
import os

def test_file_traversal():
    """测试文件遍历功能"""
    # 创建遍历器实例
    base_path = "D:\\BaiduNetdiskDownload\\法律条文\\国家级文书"  # 替换为你的实际路径
    traversal = FolderTraversal(base_path)
    
    print("=== 测试基本遍历功能 ===")
    # 1. 测试获取所有Word文档
    print("\n1. 获取所有Word文档：")
    doc_files = traversal.get_doc_files()
    for doc in doc_files:
        print(f"找到文档: {doc}")
    
    print("\n=== 测试文件类型过滤 ===")
    # 2. 测试特定文件类型过滤
    print("\n2. 仅获取.docx文件：")
    docx_files = traversal.traverse(file_types=['.docx'])
    for docx in docx_files:
        print(f"找到docx文件: {docx}")
    
    print("\n=== 测试目录排除功能 ===")
    # 3. 测试排除目录
    exclude_dirs = ['temp', 'cache']  # 替换为你想排除的目录
    print(f"\n3. 排除以下目录的文件: {exclude_dirs}")
    filtered_files = traversal.traverse(exclude_dirs=exclude_dirs)
    for file in filtered_files:
        print(f"找到文件: {file}")
    
    print("\n=== 测试文件名模式匹配 ===")
    # 4. 测试文件名模式匹配
    print("\n4. 使用通配符模式匹配：")
    pattern_files = traversal.traverse_by_name("*.docx")
    for file in pattern_files:
        print(f"匹配到文件: {file}")
    
    print("\n=== 测试自定义过滤器 ===")
    # 5. 测试自定义过滤器（例如：仅获取大于1KB的文件）
    def size_filter(file_path: str) -> bool:
        return os.path.getsize(file_path) > 1024  # 大于1KB的文件
    
    print("\n5. 使用自定义过滤器（大于1KB的文件）：")
    large_files = traversal.traverse(file_filter=size_filter)
    for file in large_files:
        size_kb = os.path.getsize(file) / 1024
        print(f"找到文件: {file} (大小: {size_kb:.2f}KB)")
    
    print("\n=== 测试组合功能 ===")
    # 6. 测试组合功能：获取特定类型且满足大小要求的文件
    print("\n6. 组合测试（Word文档且大于1KB）：")
    combined_files = traversal.traverse(
        file_types=['.doc', '.docx'],
        file_filter=size_filter
    )
    for file in combined_files:
        size_kb = os.path.getsize(file) / 1024
        print(f"找到文件: {file} (大小: {size_kb:.2f}KB)")

if __name__ == "__main__":
    try:
        test_file_traversal()
    except Exception as e:
        print(f"测试过程中发生错误: {str(e)}") 