import os
import win32com.client
import pythoncom

def convert_doc_to_docx(doc_path, docx_path=None):
    """
    将DOC文件转换为DOCX格式
    
    参数:
        doc_path (str): DOC文件的路径
        docx_path (str, optional): 输出DOCX文件的路径。如果未指定，将使用与DOC相同的名称
    
    返回:
        bool: 转换成功返回True，失败返回False
    """
    try:
        # 如果未指定docx路径，则使用doc文件名（替换扩展名）
        if docx_path is None:
            docx_path = os.path.splitext(doc_path)[0] + '.docx'
        
        # 初始化COM对象
        pythoncom.CoInitialize()
        word = win32com.client.Dispatch('Word.Application')
        
        # 设置Word不可见
        word.Visible = False
        
        # 打开DOC文档
        doc = word.Documents.Open(os.path.abspath(doc_path))
        
        # 保存为DOCX格式
        doc.SaveAs2(os.path.abspath(docx_path), FileFormat=16)
        
        # 关闭文档和Word应用
        doc.Close()
        word.Quit()
        
        return True
        
    except Exception as e:
        print(f"转换过程中出现错误: {str(e)}")
        return False
        
    finally:
        # 释放COM对象
        pythoncom.CoUninitialize()

def batch_convert_doc_to_docx(folder_path):
    """
    批量转换文件夹中的所有DOC文件为DOCX格式
    
    参数:
        folder_path (str): 包含DOC文件的文件夹路径
    
    返回:
        tuple: (成功转换数量, 失败转换数量)
    """
    success_count = 0
    failed_count = 0
    
    # 遍历文件夹中的所有文件
    for filename in os.listdir(folder_path):
        if filename.lower().endswith('.doc') and not filename.lower().endswith('.docx'):
            doc_path = os.path.join(folder_path, filename)
            docx_path = os.path.join(folder_path, filename[:-4] + '.docx')
            
            if convert_doc_to_docx(doc_path, docx_path):
                success_count += 1
                print(f"成功转换: {filename}")
            else:
                failed_count += 1
                print(f"转换失败: {filename}")
    
    return success_count, failed_count

if __name__ == "__main__":
    # 使用示例
    # 单个文件转换
    # doc_file = "example.doc"
    # if os.path.exists(doc_file):
    #     result = convert_doc_to_docx(doc_file)
    #     print(f"单个文件转换{'成功' if result else '失败'}")
    
    # 批量转换
    folder = "C:\\Users\\Admin\\Desktop\\民事判决书"
    if os.path.exists(folder):
        success, failed = batch_convert_doc_to_docx(folder)
        print(f"\n批量转换完成:")
        print(f"成功转换: {success} 个文件")
        print(f"转换失败: {failed} 个文件")
