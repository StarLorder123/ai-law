from json_utils import JsonConverter
import os
from pathlib import Path

def test_json_converter():
    """测试JSON转换功能"""
    print("=== JSON转换测试 ===")
    
    # 1. 测试对象到JSON字符串的转换
    print("\n1. Python对象转JSON字符串测试")
    test_obj = {
        "name": "张三",
        "age": 25,
        "hobbies": ["读书", "运动"],
        "info": {
            "city": "北京",
            "job": "程序员"
        }
    }
    
    # 普通转换
    json_str = JsonConverter.to_json_string(test_obj)
    print("普通JSON字符串:")
    print(json_str)
    
    # 格式化转换
    pretty_json_str = JsonConverter.to_json_string(test_obj, pretty=True)
    print("\n格式化JSON字符串:")
    print(pretty_json_str)
    
    # 2. 测试JSON字符串到对象的转换
    print("\n2. JSON字符串转Python对象测试")
    obj = JsonConverter.from_json_string(json_str)
    print("转换后的对象:")
    print(f"姓名: {obj['name']}")
    print(f"年龄: {obj['age']}")
    print(f"爱好: {', '.join(obj['hobbies'])}")
    
    # 3. 测试文件保存和加载
    print("\n3. JSON文件保存和加载测试")
    # 设置测试文件路径
    test_file = "test_output/test.json"
    
    # 保存到文件
    saved_path = JsonConverter.save_to_file(test_obj, test_file)
    print(f"文件已保存到: {saved_path}")
    
    # 从文件加载
    loaded_obj = JsonConverter.load_from_file(test_file)
    print("从文件加载的对象:")
    print(JsonConverter.to_json_string(loaded_obj, pretty=True))
    
    # 4. 测试JSON对象合并
    print("\n4. JSON对象合并测试")
    obj1 = {"name": "张三", "age": 25}
    obj2 = {"city": "北京", "job": "程序员"}
    merged_obj = JsonConverter.merge_json_objects(obj1, obj2)
    print("合并后的对象:")
    print(JsonConverter.to_json_string(merged_obj, pretty=True))
    
    # 5. 测试JSON字符串验证
    print("\n5. JSON字符串验证测试")
    valid_json = '{"name": "张三", "age": 25}'
    invalid_json = '{"name": "张三", age: 25'  # 缺少引号和大括号
    
    print(f"有效JSON字符串验证结果: {JsonConverter.validate_json_string(valid_json)}")
    print(f"无效JSON字符串验证结果: {JsonConverter.validate_json_string(invalid_json)}")

if __name__ == "__main__":
    try:
        test_json_converter()
    except Exception as e:
        print(f"测试过程中发生错误: {str(e)}") 