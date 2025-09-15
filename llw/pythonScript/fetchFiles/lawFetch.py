from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.select import Select
import time
import os
import shutil

# 初始化selenium
url = 'https://flk.npc.gov.cn/list.html?sort=true&type=flfg&xlwj=02,03,04,05,06,07,08'
# url = 'https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=4b79e471835588ebfe9eb19189df3944&s8=02'

option = webdriver.ChromeOptions()
option.add_argument('--start-maximized')
option.add_experimental_option('excludeSwitches', ['enable-automation'])

# 'profile.default_content_settings.popups': 0  ==  禁用弹出窗口
# 'download.default_directory': 'D:\Desktop\wenshu'  == 设置默认下载路径
# 'profile.default_content_setting_values.automatic_downloads': 1 == 并设置自动下载的选项
prefs = {'profile.default_content_settings.popups': 0,
         'download.default_directory': 'D:\laws',  # 设置自己的下载路径
         'profile.default_content_setting_values.automatic_downloads': 1}
option.add_experimental_option('prefs', prefs)


driver = webdriver.Chrome(options=option)
# 设置打开的浏览器窗口最大化
driver.maximize_window()
driver.set_page_load_timeout(30)
driver.get(url)

time.sleep(3)

baseLocationUp = '//*[@id="flData"]/tr'
baseLocationDown = '/td[2]/h1/ul/li'

homeWindow = driver.window_handles[-1]
for j in range(46):
    for i in range(10):
        driver.switch_to.window(homeWindow)
        time.sleep(20)
        law = driver.find_element(By.XPATH, baseLocationUp + '[' + str(i + 1) + ']' + baseLocationDown)
        lawName=law.text
        print(law.text)
        law.click()
        time.sleep(20)

        _lastWindow = driver.window_handles[-1]
        driver.switch_to.window(_lastWindow)
        driver.find_element(By.XPATH, '//*[@id="downLoadFile"]').click()
        time.sleep(2)

        # 获取下载目录中的最新文件
        download_dir = "D:\laws"
        files = os.listdir(download_dir)
        latest_file = max(files, key=lambda x: os.path.getctime(os.path.join(download_dir, x)))
        # 重命名文件
        os.rename(os.path.join(download_dir, latest_file), os.path.join(download_dir, lawName + '.docx'))
        driver.close()
    # 切换下一页
    driver.switch_to.window(homeWindow)
    driver.find_element(By.LINK_TEXT, '下一页').click()
    time.sleep(20)

driver.quit()
