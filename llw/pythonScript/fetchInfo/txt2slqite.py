import sqlite3
import json
import re

def create_table(conn):
    # result = {
    #     "ftmc": "",  # 法规标题
    #     "fgzh": "",  # 法规文号
    #     "fbjg": "",  # 颁布机构
    #     "fbrq": "",  # 颁布日期
    #     "tgrq": "",  # 通过日期
    #     "sxrq": "",  # 生效日期
    #     "preface": "",  # 前言
    #     "presidentOrder": {
    #         "orderName": "中华人民共和国主席令",
    #         "orderNo": "",
    #         "orderContent": "",
    #         "signatory": "",
    #         "signDate": ""
    #     },
    #     "hasEdition": False,  # 是否有编，这里py脚本没有跑出来都是false要多判断一层
    #     "content": []  # 内容结构
    # }
    """创建数据库表结构"""
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS laws (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            ftmc TEXT,
            fgzh TEXT,
            fbjg TEXT,
            fbrq TEXT,
            tgrq TEXT,
            sxrq TEXT,
            preface TEXT,
            president_order_name TEXT,
            president_order_no TEXT,
            president_order_content TEXT,
            president_signatory TEXT,
            president_sign_date TEXT,
            has_edition INTEGER,
            content TEXT
        )
    ''')
    conn.commit()


def insert_data(conn, data):
    content = data.get('content', [])
    # print(content)
    if content is None or len(content) == 0:
        print("content为空")
        return

    """插入数据到数据库"""
    cursor = conn.cursor()
    try:
        # 提取presidentOrder字段
        president_order = data.get('presidentOrder', {})

        # 判断是够有编
        if content[0].get('type') == 'edition':
            hasEdition = True
        else:
            hasEdition = False

        # 序列化content列表
        content_str = json.dumps(data.get('content', []),ensure_ascii=False)
        content_str = content_str.replace('\u2002', '')

        ftmc = data.get('ftmc', '')
        # 匹配中文括号【】及其内容
        pattern_cn = r'【[^】]*】'
        # 匹配英文括号()及其内容
        pattern_en = r'（[^）]*）'

        # 组合正则表达式，同时移除两种括号
        combined_pattern = f'{pattern_cn}|{pattern_en}'
        # 执行替换操作
        ftmc = re.sub(combined_pattern, '', ftmc)

        # 准备插入的数据
        insert_values = (
            ftmc,
            data.get('fgzh', ''),
            data.get('fbjg', ''),
            data.get('fbrq', ''),
            data.get('tgrq', ''),
            data.get('sxrq', ''),
            data.get('preface', ''),
            president_order.get('orderName', ''),
            president_order.get('orderNo', ''),
            president_order.get('orderContent', ''),
            president_order.get('signatory', ''),
            president_order.get('signDate', ''),
            1 if hasEdition else 0,
            content_str
        )

        # 执行插入操作
        cursor.execute('''
            INSERT INTO laws (
                ftmc, fgzh, fbjg, fbrq, tgrq, sxrq, preface,
                president_order_name, president_order_no, president_order_content,
                president_signatory, president_sign_date, has_edition, content
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ''', insert_values)

        conn.commit()
    except Exception as e:
        print(f"插入数据时发生错误: {e}")


def main():
    # 连接到SQLite数据库（如果不存在会自动创建）
    conn = sqlite3.connect('laws_dict.db',detect_types=sqlite3.PARSE_DECLTYPES,
        isolation_level=None)
    # 设置连接编码（SQLite 3.32+支持）
    conn.execute('PRAGMA encoding = "UTF-8";')

    # 创建表
    create_table(conn)

    # 读取并解析txt文件
    with open('law_results.txt', 'r', encoding='utf-8') as file:
        for line in file:
            line = line.strip()
            if line:
                try:
                    # print(line)
                    data = json.loads(line)
                    insert_data(conn, data)
                except json.JSONDecodeError as e:
                    print(f"JSON解析失败: {e}，行内容: {line}")

    # 关闭数据库连接
    conn.close()


if __name__ == "__main__":
    main()
