import os
import json
import time
import pickle
from datetime import datetime
import logging
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import requests
import sqlite3
from typing import Dict, Any, List
from bs4 import BeautifulSoup


class LawDataFetcher:
    def __init__(self, username=None, password=None):
        self.base_url = "https://bigdata.lawyee.net"
        self.login_url = "https://bigdata.lawyee.net/login"
        self.api_url = "https://bigdata.lawyee.net/dsjwjfx/ajts/getListByDataSource"

        self.username = "levi"
        self.password = "Runningwei@6"

        self.session = requests.Session()
        self.setup_logging()

        # Chrome配置
        self.chrome_options = webdriver.ChromeOptions()
        self.chrome_options.add_argument('--no-sandbox')
        self.chrome_options.add_argument('--disable-dev-shm-usage')

        # cookie文件路径
        self.cookie_file = 'law_cookies.pkl'
        self.session_file = 'law_session.pkl'

        # 初始化数据库
        self.init_db()

    def init_db(self):
        """初始化SQLite数据库"""
        try:
            self.conn = sqlite3.connect('laws_document.db')
            self.cursor = self.conn.cursor()

            # 新增解析结果表
            self.cursor.execute('''
                CREATE TABLE IF NOT EXISTS parsed_documents (
                    id TEXT PRIMARY KEY,
                    ah TEXT,        -- 案号
                    spcx TEXT,      -- 审判程序
                    ajlx TEXT,      -- 案件类型
                    sycx TEXT,      -- 适用程序
                    wslx TEXT,      -- 文书类型
                    jafs TEXT,      -- 结案方式
                    fy_name TEXT,   -- 法院名称
                    fy_level TEXT,  -- 法院层级
                    fy_province TEXT, -- 法院省份
                    fy_city TEXT,    -- 法院城市
                    larq DATE,      -- 立案日期
                    cprq DATE,      -- 裁判日期
                    content TEXT    -- 文书全文
                )
            ''')
            self.conn.commit()
        except Exception as e:
            logging.error(e)

    def parse_document(self, raw_data):
        """解析原始数据为结构化格式"""
        mapping = {
            'ah': ('案号',),
            'spcx': ('审判程序',),  # 精确匹配
            'ajlx': ('案件类型',),
            'sycx': ('适用程序',),
            'wslx': ('文书类型',),
            'jafs': ('结案方式',),
            'fy_name': ('法院名称',),
            'fy_level': ('法院层级',),
            'fy_province': ('法院所在省市',),
            'fy_city': ('法院所在地市级',),
            'larq': ('立案日期',),
            'cprq': ('裁判日期',)
        }

        result = {}
        for item in raw_data.get('result', []):
            # 提取冒号前的字段名称并去空格
            field_name = item.get('name', '').split('：')[0].strip()
            
            for field, keywords in mapping.items():
                if any(kw in field_name for kw in keywords):
                    result[field] = item.get('value', '')
                    break

        # 修正日期处理逻辑
        for field in ['larq', 'cprq']:
            if result.get(field):
                try:
                    # 原始日期格式为 yyyymmdd（如 20240801）
                    date_str = result[field].strip()
                    if len(date_str) == 8 and date_str.isdigit():
                        # 转换为 yyyy-mm-dd 格式
                        result[field] = f"{date_str[:4]}-{date_str[4:6]}-{date_str[6:8]}"
                    else:
                        result[field] = None
                except Exception as e:
                    logging.error(f"日期格式转换失败[{field}]: {result[field]} - {str(e)}")
                    result[field] = None

        # 处理文书内容（增强块级元素换行处理）
        html_content = raw_data.get('qw', '')
        soup = BeautifulSoup(html_content, 'html.parser')

        # 处理块级元素换行（新增部分）
        block_tags = ['div', 'p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'li', 'table']
        for tag in soup.find_all(block_tags):
            tag.append('\n')  # 在块级元素末尾添加换行符

        # 处理原生换行标签
        for br in soup.find_all('br'):
            br.replace_with('\n')

        # 获取纯净文本并压缩多余空行
        cleaned_text = soup.get_text()
        cleaned_text = '\n'.join(line.strip() for line in cleaned_text.splitlines() if line.strip())
        result['content'] = cleaned_text
        
        return result


    def save_parsed_data(self, data_id: str, parsed_data: Dict) -> bool:
        """保存解析后的结构化数据"""
        try:
            self.cursor.execute('''
                INSERT OR REPLACE INTO parsed_documents 
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)''',
                (
                    data_id,
                    parsed_data.get('ah'),
                    parsed_data.get('spcx'),
                    parsed_data.get('ajlx'),
                    parsed_data.get('sycx'),
                    parsed_data.get('wslx'),
                    parsed_data.get('jafs'),
                    parsed_data.get('fy_name'),
                    parsed_data.get('fy_level'),
                    parsed_data.get('fy_province'),
                    parsed_data.get('fy_city'),
                    parsed_data.get('larq'),
                    parsed_data.get('cprq'),
                    parsed_data.get('content')
                )
            )
            self.conn.commit()
            return True
        except Exception as e:
            logging.error(f"保存解析数据失败: {str(e)}")
            return False

    def setup_logging(self):
        """设置日志配置"""
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler('law_data_fetch.log', encoding='utf-8'),
                logging.StreamHandler()
            ]
        )

    def save_cookies_and_session(self, driver):
        """保存cookies和session"""
        try:
            # 保存cookies
            cookies = driver.get_cookies()
            with open(self.cookie_file, 'wb') as f:
                pickle.dump(cookies, f)

            # 保存session
            with open(self.session_file, 'wb') as f:
                pickle.dump(self.session.cookies, f)

            logging.info("成功保存cookies和session")
            return True
        except Exception as e:
            logging.error(f"保存cookies和session失败: {str(e)}")
            return False

    def load_cookies_and_session(self):
        """加载所有会话信息"""
        try:
            if os.path.exists(self.cookie_file) and os.path.exists(self.session_file):
                # 加载认证信息
                with open(self.cookie_file, 'rb') as f:
                    self.auth_info = pickle.load(f)

                # 加载session
                with open(self.session_file, 'rb') as f:
                    self.session.cookies.update(pickle.load(f))

                # 验证信息完整性
                required_keys = ['web_cookie', 'token', 'userInfo']
                if not all(key in self.auth_info for key in required_keys):
                    logging.error("会话信息不完整")
                    return False

                logging.info("成功加载所有会话信息")
                return True
            return False
        except Exception as e:
            logging.error(f"加载会话信息失败: {str(e)}")
            return False

    def get_web_cookie(self, driver):
        """获取web cookie值"""
        try:
            web_cookie = None
            cookies = driver.get_cookies()
            for cookie in cookies:
                if cookie['name'] == 'web':
                    web_cookie = cookie['value']
                    break
            return web_cookie
        except Exception as e:
            logging.error(f"获取web cookie失败: {str(e)}")
            return None

    def login(self):
        """使用Selenium进行登录"""
        driver = None
        try:
            driver = webdriver.Chrome(options=self.chrome_options)
            driver.get(self.login_url)

            driver.find_element(By.CLASS_NAME, "login-btn").click()

            # 等待登录表单加载
            wait = WebDriverWait(driver, 10)
            username_input = wait.until(EC.presence_of_element_located((By.NAME, "username")))
            password_input = driver.find_element(By.NAME, "password")

            # 输入账号密码
            username_input.send_keys(self.username)
            password_input.send_keys(self.password)

            time.sleep(5)

            # 点击登录按钮
            login_button = driver.find_element(By.ID, "btnSubmit")
            login_button.click()

            # 等待登录成功并跳转
            time.sleep(5)

            # 获取所有会话信息
            cookies = driver.get_cookies()
            local_storage = driver.execute_script("return window.localStorage;")
            session_storage = driver.execute_script("return window.sessionStorage;")

            # 获取web cookie和其他关键cookie
            web_cookie = self.get_web_cookie(driver)
            if not web_cookie:
                logging.error("未能获取web cookie")
                return False

            # 获取token和其他认证信息
            token = driver.execute_script("return localStorage.getItem('token');")
            user_info = driver.execute_script("return localStorage.getItem('userInfo');")

            # 保存完整的认证信息
            self.auth_info = {
                'cookies': cookies,
                'web_cookie': web_cookie,
                'localStorage': local_storage,
                'sessionStorage': session_storage,
                'token': token,
                'userInfo': user_info,
                'headers': {
                    'Authorization': token,
                    'Cookie': f'web={web_cookie}'
                }
            }

            # 更新session的cookies
            self.session.cookies.clear()
            for cookie in cookies:
                self.session.cookies.set(cookie['name'], cookie['value'])

            # 保存到文件
            with open(self.cookie_file, 'wb') as f:
                pickle.dump(self.auth_info, f)

            # 保存session状态
            with open(self.session_file, 'wb') as f:
                pickle.dump(self.session.cookies, f)

            logging.info("登录成功并保存所有会话信息")
            return True

        except Exception as e:
            logging.error(f"登录过程出错: {str(e)}")
            return False
        finally:
            if driver:
                driver.quit()

    def check_login_status(self):
        """检查登录状态"""
        try:
            response = self.session.get(f"{self.base_url}/homePage")
            return response.status_code == 200 and "login" not in response.url
        except:
            return False

    def fetch_detail(self, row_id):
        """获取法规详细信息"""
        try:
            detail_url = "https://bigdata.lawyee.net/dsjwjfx/qw/ybxml"
            
            headers = {
                'Accept': 'application/json, text/javascript, */*; q=0.01',
                'Accept-Encoding': 'gzip, deflate, br, zstd',
                'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
                'Connection': 'keep-alive',
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'Cookie': f'web={self.auth_info["web_cookie"]}',
                'Host': 'bigdata.lawyee.net',
                'Origin': 'https://bigdata.lawyee.net',
                'Referer': f'https://bigdata.lawyee.net/dsjwjfx/qw/pop-qw?rowKey={row_id}&ws_dl=flfg&type=1',
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
                'X-Requested-With': 'XMLHttpRequest'
            }
            
            payload = {
                'rowkey': row_id,
                'dl': 'fyajws',
                'highlightWord': '',
                'type': '1'
      }
            
            response = self.session.post(
                detail_url,
                headers=headers,
                data=payload,
                timeout=30
            )
            
            if response.status_code == 200:
                data = json.loads(response.text)
                # 保存原始数据
                # if self.save_to_db(row_id, 'detail', data):
                #     logging.info(f"成功保存法规ID {row_id} 的详细信息")
                
                # 新增解析逻辑
                parsed_data = self.parse_document(data)
                if self.save_parsed_data(row_id, parsed_data):
                    logging.info(f"成功解析并存储法规ID {row_id} 的结构化数据")
                
                return data
            else:
                logging.error(f"获取详细信息失败: {response.status_code}")
                return None
            
        except Exception as e:
            logging.error(f"获取详细信息时出错: {str(e)}")
            return None

    def fetch_data(self, page=1, page_size=100):
        """获取法律数据列表"""
        try:
            if not hasattr(self, 'auth_info') or not self.auth_info.get('web_cookie'):
                if not self.load_cookies_and_session():
                    logging.error("缺少认证信息")
                    return None

            headers = {
                'Accept': '*/*',
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'Cookie': f'web={self.auth_info["web_cookie"]}',
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
            }

            payload = {
                "dataSource": "fyajws",
                "pageNum": page,
                "pageSize": page_size,
                "isSearch": "false",
                "isChartSearch": "false",
                'dataQueryCond':"eyJjb25kaXRpb24iOiJBTkQiLCJydWxlcyI6W3siaWQiOiI0MyIsImZpZWxkIjoiNDMiLCJ0eXBlIjoic3RyaW5nIiwiaW5wdXQiOiJjaGVja2JveCIsIm9wZXJhdG9yIjoiaW4iLCJ2YWx1ZSI6WyIxIiwiMiIsIjMiXX0seyJpZCI6IjI0IiwiZmllbGQiOiIyNCIsInR5cGUiOiJzdHJpbmciLCJpbnB1dCI6InRleHQiLCJvcGVyYXRvciI6ImNvbnRhaW5zIiwidmFsdWUiOiLljJfkuqwifV0sInZhbGlkIjp0cnVlfQ=="
            }

            response = self.session.post(
                self.api_url,
                headers=headers,
                data=payload,
                timeout=30
            )

            if response.status_code == 401 or response.status_code == 403 or response.text.__contains__("<!DOCTYPE html>"):
                logging.warning("登录已过期或无权限，尝试重新登录")
                if self.login():
                    headers['Cookie'] = f'web={self.auth_info["web_cookie"]}'
                    response = self.session.post(
                        self.api_url,
                        headers=headers,
                        data=payload,
                        timeout=30
                    )
                else:
                    logging.error("重新登录失败")
                    return None

            if response.status_code == 200:
                data = json.loads(response.text)
                if isinstance(data, dict) and 'data' in data:
                    return data['data']
                else:
                    logging.error(f"API返回数据格式错误: {data}")
                    return None
            else:
                logging.error(f"HTTP请求失败: {response.status_code}")
                return None

        except Exception as e:
            logging.error(f"请求异常: {str(e)}")
            return None

    def batch_fetch(self, start_page=1, end_page=1):
        """批量获取数据"""
        try:
            for page in range(start_page, end_page + 1):
                logging.info(f"正在获取第 {page} 页数据")
                
                page_data = self.fetch_data(page=page, page_size=100)
                
                if not page_data:
                    logging.error(f"获取第 {page} 页数据失败")
                    continue

                for law_item in page_data.get('list', []):
                    try:
                        row_id = law_item.get('id')
                        if not row_id:
                            continue


                        logging.info(f"正在获取法律ID: {row_id} 的详细信息")
                        self.fetch_detail(row_id)
                        time.sleep(2)

                    except Exception as e:
                        logging.error(f"处理法律ID: {row_id} 时出错: {str(e)}")
                        continue

                logging.info(f"第 {page} 页数据处理完成")
                time.sleep(2)

            logging.info("批量获取数据完成")
            return True

        except Exception as e:
            logging.error(f"批量获取数据时出错: {str(e)}")
            return False

    def __del__(self):
        """析构函数，确保数据库连接正确关闭"""
        if hasattr(self, 'conn'):
            self.conn.close()

def main():
    username = "levi"
    password = "Runningwei@6"

    fetcher = LawDataFetcher(username, password)

    try:
        logging.info("开始获取法律数据...")
        success = fetcher.batch_fetch(start_page=2, end_page=600)
        
        if success:
            logging.info("数据获取完成")
        else:
            logging.error("数据获取失败")

    except Exception as e:
        logging.error(f"程序执行出错: {str(e)}")

if __name__ == "__main__":
    main()
