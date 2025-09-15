import os
import csv
import json
from pathlib import Path
from typing import Dict, List, Any
from datetime import datetime
import logging
from tqdm import tqdm
import html
import re

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    filename='process.log'
)


def transform_row_to_json(row: Dict) -> Dict[str, Any]:
    """
    将CSV的一行数据转换为指定的JSON格式
    """
    wsnr = row.get("全文", "")
    wsnr = html.unescape(wsnr)
    # 使用正则表达式匹配所有非 ASCII 字符和不可打印字符
    wsnr = re.sub(r'[^\u4e00-\u9fff\u3000-\u303F\uFF00-\uFFEF\x00-\x7F]+', ' ', wsnr)

    link = row.get("\ufeff原始链接", "")
    if link is None or "".__eq__(link):
        link = row.get("原始链接", "")

    wsnr_nonlank = wsnr.replace(" ", "")
    wsnr_nonlank = wsnr_nonlank.replace("\t", "")
    # 生成文书名称（wsmc）
    try:
        wsmc = wsnr_nonlank.split("书（")[0] + "书"
        if len(wsnr_nonlank.split("书（")) == 1:
            wsmc = wsnr_nonlank.split("书(")[0] + "书"
        wsmc = wsmc.split("法院")[1]
        wsmc = wsmc.replace(" ", "")  # 删除空格
    except IndexError:
        # 如果无法解析出文书名称，使用默认值
        wsmc = ""

    # 生成案件名称
    try:
        ajmc = row.get("案件名称", "")
        ajmc = ajmc.replace(wsmc, "")
        ajmc = ajmc.replace("一审", "")
        ajmc = ajmc.replace("二审", "")
        ajmc = ajmc.replace("判决书", "")
    except IndexError:
        # 如果无法解析出文书名称，使用默认值
        ajmc = ""

    # 消除文书内容的过多空格
    wsnr = wsnr.replace("\t", "")

    # 消除文书内容的脏水印
    wsnr = wsnr.replace("更多数据：www.macrodatas.cn", "")
    wsnr = wsnr.replace("来源：马 克 数 据 网", "")
    wsnr = wsnr.replace("微信公众号“马克 数据网”", "")
    wsnr = wsnr.replace("来自：www.macrodatas.cn", "")
    wsnr = wsnr.replace("来源：马 克 数 据 网", "")
    wsnr = wsnr.replace("百度搜索“马克数据网”", "")
    wsnr = wsnr.replace("百度搜索“马 克 数 据 网”", "")
    wsnr = wsnr.replace("搜索“马 克 数 据 网”", "")
    wsnr = wsnr.replace("关注微信公众号“马克数据网”", "")
    wsnr = wsnr.replace("关注公众号“马克数据网”", "")
    wsnr = wsnr.replace("关注公众号“马 克 数 据 网”", "")
    wsnr = wsnr.replace("微信公众号“马克数据网”", "")
    wsnr = wsnr.replace("微信公众号“马克 数据网”", "")
    wsnr = wsnr.replace("来自马克数据网", "")
    wsnr = wsnr.replace("来自马 克 数 据 网", "")
    wsnr = wsnr.replace("来自马克 数据网", "")
    wsnr = wsnr.replace("来源：百度“马 克 数据网”", "")
    wsnr = wsnr.replace("马克数据网", "")
    wsnr = wsnr.replace("马克 数据网", "")
    wsnr = wsnr.replace("马 克 数 据 网", "")
    wsnr = wsnr.replace("www.macrodatas.cn”", "")
    wsnr = wsnr.replace("更多数据", "")
    wsnr = wsnr.replace("关注微信公众号", "")
    wsnr = wsnr.replace("来源：百度 马 克 数据网", "")
    wsnr = wsnr.replace("百度搜索", "")
    wsnr = wsnr.replace("来源：", "")
    wsnr = wsnr.replace("来源：微信公众号", "")
    wsnr = wsnr.replace("来源：百度 马 克 数据网", "")
    wsnr = wsnr.replace("&hellip;", "")
    wsnr = wsnr.replace("{C}", "")

    return {
        "wsmc": wsmc,  # 文书名称
        "ah": row.get("案号", ""),  # 案号
        "ajlx": row.get("案件类型", ""),  # 案件类型
        "ssdq": row.get("所属地区", ""),  # 所属地区
        "ajmc": ajmc,  # 案件名称
        "sj": row.get("裁判日期", ""),  # 时间
        "fymc": row.get("法院", ""),  # 法院名称
        "slcx": row.get("审理程序", ""),  # 受理程序
        "dsr": row.get("当事人", []),  # 当事人
        "content": {
            "ay": row.get("案由", ""),  # 案由
            "wsnc": wsnr,  # 文书内容
            "flyj": row.get("法律依据", "")  # 法律依据
        },
        "extension": {
            "link": link,  # 链接
            "filename": row.get("案号", "") + wsmc + ".docx"  # 文件名
        }
    }


def get_all_csv_files(root_path: str) -> List[Path]:
    """
    获取所有子文件夹中的CSV文件
    """
    csv_files = []
    root = Path(root_path)

    for folder in root.iterdir():
        if folder.is_dir():
            csv_files.extend(folder.glob("*.csv"))

    return csv_files


def process_single_csv(csv_file: Path, output_file: str, chunk_size: int = 1000):
    """
    处理单个CSV文件，使用分块读取来节省内存

    Args:
        csv_file: CSV文件路径
        output_file: 输出文件路径
        chunk_size: 每次读取的行数
    """
    try:
        # 获取文件大小用于显示进度
        file_size = csv_file.stat().st_size
        processed_size = 0

        with open(csv_file, 'r', encoding='utf-8') as f:
            # 读取头部获取字段名
            header = next(csv.reader(f))

            with open(output_file, 'a', encoding='utf-8') as out_f:
                # 用于批量处理的缓冲区
                buffer = []

                # 使用 tqdm 显示进度
                with tqdm(total=file_size, unit='B', unit_scale=True, desc=f'Processing {csv_file.name}') as pbar:
                    for line in f:
                        # 手动解析 CSV 行
                        try:
                            row = next(csv.reader([line]))
                            json_data = transform_row_to_json(dict(zip(header, row)))
                            buffer.append(json.dumps(json_data, ensure_ascii=False))
                        except Exception as e:
                            logging.error(f"Error parsing row in {csv_file}: {e}")
                            continue

                        # 当缓冲区达到指定大小时写入文件
                        if len(buffer) >= chunk_size:
                            out_f.write('\n'.join(buffer) + '\n')
                            buffer.clear()

                        # 更新进度条
                        processed_size += len(line.encode('utf-8'))
                        pbar.update(len(line.encode('utf-8')))

                    # 写入剩余的数据
                    if buffer:
                        out_f.write('\n'.join(buffer) + '\n')
                        buffer.clear()

    except Exception as e:
        logging.error(f"Error processing file {csv_file}: {e}")


def process_csv_files(root_path: str, output_file: str):
    """
    处理所有CSV文件
    """
    # 获取所有csv文件
    csv_files = get_all_csv_files(root_path)

    if not csv_files:
        logging.warning("No CSV files found in the directories")
        return

    logging.info(f"Found {len(csv_files)} CSV files to process")

    # 写入开始时间
    with open(output_file, 'a', encoding='utf-8') as out_f:
        start_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        # out_f.write(f"# Processing started at {start_time}\n")

    # 逐个处理文件
    for csv_file in csv_files:
        logging.info(f"Starting to process: {csv_file}")
        process_single_csv(csv_file, output_file)
        logging.info(f"Finished processing: {csv_file}")

    # 写入结束时间
    with open(output_file, 'a', encoding='utf-8') as out_f:
        end_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        # out_f.write(f"\n# Processing completed at {end_time}\n")


def main():
    root_path = "F:\\BaiduNetdiskDownload\\中国裁判文书网\\2020年裁判文书数据"  # 根目录路径
    output_file = "G:\\wenshu_output_example.txt"  # 输出文件路径

    # 确保输出文件所在的文件夹存在
    os.makedirs(os.path.dirname(output_file) if os.path.dirname(output_file) else '.', exist_ok=True)

    # 处理文件
    process_csv_files(root_path, output_file)


if __name__ == "__main__":
    main()
