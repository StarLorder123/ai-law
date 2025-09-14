from doc_reader import DocReader
from json_utils import JsonConverter

try:
    # 读取txt文件内容
    content = DocReader.read_txt("./data/testlaw.txt")
    
    # 使用 JsonConverter 将字符串转换为 JSON 对象
    json_obj = JsonConverter.from_json_string(content)
    
    print("转换后的JSON对象：")
    print(json_obj)
    print(json_obj['ftmc'])
    
except FileNotFoundError:
    print("找不到文件：testlaw.txt")
except UnicodeDecodeError as e:
    print(f"文件编码错误：{e}")
except Exception as e:
    print(f"处理过程中出现错误：{e}")
