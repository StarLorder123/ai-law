import requests
import os
import time
from fake_useragent import UserAgent

filename = '../法律条文\\'
if not os.path.exists(filename):
    os.mkdir(filename)

# https://flk.npc.gov.cn/api/?type=flfg&searchType=title；vague&sortTr=f_bbrq_s；desc&gbrqStart=&gbrqEnd=&sxrqStart=&sxrqEnd=&sort=true&page=1&size=10&_=1732894909082
home_url = 'https://flk.npc.gov.cn/api/?'
# &=02&=03&xlwj=04&xlwj=05&xlwj=06&xlwj=07&xlwj=08&
ori_data = {
    # 'type': 'flfg',
    'searchType': 'title;vague',
    'sortTr': 'f_bbrq_s;desc',
    'gbrqStart': '',
    'gbrqEnd': '',
    'sxrqStart': '',
    'sxrqEnd': '',
    'sort': 'true',
    'page': '1',
    'size': '10',
    '_': '1704800783355',
}
ua = UserAgent()

proxypool_url = 'http://127.0.0.1:5555/random'
headers = {
    'User-Agent': ua.random
}


# 获取随机代理
def get_random_proxy():
    return requests.get(proxypool_url).text.strip()


def repeat_request(req_url, request_data, func):
    keep_request = True
    while keep_request:
        try:
            proxy = get_random_proxy()
            proxies = {'http': 'http://' + proxy}
            if 'get'.__eq__(func):
                resp_data = requests.get(url=req_url, params=request_data, headers=headers, proxies=proxies, timeout=5)
            else:
                resp_data = requests.post(url=req_url, data=request_data, headers=headers, proxies=proxies, timeout=5)
            keep_request = False
            return resp_data

        except:
            print("重新连接")
            time.sleep(1)


# "https://wb.flk.npc.gov.cn/flfg/WORD/15526420544a4ad18df391c0d8a88a6b.docx"
for i in range(1797, 2197):
    ori_data['page'] = i + 1
    ori_data['_'] = str(i + 1704800783355)

    data_json = repeat_request(home_url, ori_data, 'get').json()
    print(data_json)

    data_list = data_json['result']['data']
    print('new page', ori_data['page'])

    for item in data_list:
        file_id = item['id']
        title = item['title']
        detail_url = 'https://flk.npc.gov.cn/api/detail'
        fetch_data = {
            'id': file_id
        }
        new_data = repeat_request(detail_url, fetch_data, 'post').json()

        if new_data['result']['body'][0]['path']:
            down_load = 'https://wb.flk.npc.gov.cn' + new_data['result']['body'][0]['path']
            name = new_data['result']['body'][0]['path'].split('.')[-1]
            content = repeat_request(down_load, '', 'get').content
            with open('法律条文\\' + title + '.' + name, mode='wb') as f:
                f.write(content)
            print(title, down_load, name)
            time.sleep(1)
