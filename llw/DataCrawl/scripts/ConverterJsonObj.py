from doc_reader.docx_reader import DocReader
from json_utils.json_converter import JsonConverter
import uuid

def generate_long_uuid() -> str:
    """生成更长的UUID并去除破折号"""
    # 生成两个UUID并组合，然后去除所有破折号
    uuid1 = str(uuid.uuid4())
    uuid2 = str(uuid.uuid4())
    # 组合两个UUID并去除破折号
    return (uuid1 + uuid2).replace('-', '')

def process_law_text(file_path: str) -> dict:
    """
    处理法律文本文件：读取文件，转换为JSON对象，并为subcontent添加UUID
    
    Args:
        file_path (str): 文本文件路径
        
    Returns:
        dict: 处理后的JSON对象
        
    Raises:
        FileNotFoundError: 文件不存在时
        UnicodeDecodeError: 文件编码错误时
        KeyError: JSON结构不正确时
        Exception: 其他处理错误
    """
    try:
        # 读取txt文件内容
        content = DocReader.read_txt(file_path)
        
        # 使用 JsonConverter 将字符串转换为 JSON 对象
        json_obj = JsonConverter.from_json_string(content)
        
        # 遍历 content 数组
        for content_item in json_obj['content']:
            # 检查并遍历 subcontent 数组
            if 'subcontent' in content_item:
                # 创建新的 subcontent 数组
                new_subcontent = []
                for text in content_item['subcontent']:
                    # 为每个文本创建新的对象结构，使用更长的UUID
                    subcontent_obj = {
                        'text': text,
                        'uuid': generate_long_uuid()
                    }
                    new_subcontent.append(subcontent_obj)
                # 用新的对象数组替换原来的字符串数组
                content_item['subcontent'] = new_subcontent
        
        return json_obj
        
    except FileNotFoundError:
        raise FileNotFoundError(f"找不到文件：{file_path}")
    except UnicodeDecodeError as e:
        raise UnicodeDecodeError(f"文件编码错误：{e}")
    except KeyError as e:
        raise KeyError(f"JSON对象结构错误：{e}")
    except Exception as e:
        raise Exception(f"处理过程中出现错误：{e}")

# 使用示例
if __name__ == "__main__":
    try:
        result = process_law_text("testlaw.txt")
        
        print("处理后的JSON对象：")
        print(result)
        
        # 验证结果（可选）
        print("\n验证第一个subcontent的结构:")
        if len(result['content']) > 0 and 'subcontent' in result['content'][0]:
            first_subcontent = result['content'][0]['subcontent'][0]
            print(f"UUID: {first_subcontent['uuid']}")
            print(f"UUID长度: {len(first_subcontent['uuid'])}")
            print(f"Text: {first_subcontent['text']}")
            
    except Exception as e:
        print(f"错误：{e}")