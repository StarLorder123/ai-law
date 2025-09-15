from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.select import Select
import time

# 初始化selenium
url = 'https://wenshu.court.gov.cn/website/wenshu/181029CR4M5A62CH/index.html?'
# url = 'https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?pageId=4b79e471835588ebfe9eb19189df3944&s8=02'

option = webdriver.ChromeOptions()
option.add_argument('--start-maximized')
option.add_experimental_option('excludeSwitches', ['enable-automation'])

# 'profile.default_content_settings.popups': 0  ==  禁用弹出窗口
# 'download.default_directory': 'D:\Desktop\wenshu'  == 设置默认下载路径
# 'profile.default_content_setting_values.automatic_downloads': 1 == 并设置自动下载的选项
prefs = {'profile.default_content_settings.popups': 0,
		 'download.default_directory': 'D:\wenshu',		# 设置自己的下载路径
         'profile.default_content_setting_values.automatic_downloads': 1}
option.add_experimental_option('prefs', prefs)

driver = webdriver.Chrome(options=option)
# 设置打开的浏览器窗口最大化
driver.maximize_window()
driver.set_page_load_timeout(30)
driver.get(url)

# 转到登录界面自动输入手机号密码进行登录
driver.find_element(By.XPATH, '//*[@id="loginLi"]/a').click()
text = driver.page_source
time.sleep(10)  # 等待页面渲染
# 进入iframe框
iframes = driver.find_elements(By.TAG_NAME, 'iframe')
if iframes:
    driver.switch_to.frame(iframes[0])
else:
    print("No iframe found")
    driver.quit()
    exit()

# 下面的‘手机号’‘密码’输入自己中国裁判文书网注册的真实手机号密码
username = driver.find_element(By.XPATH, '//*[@id="root"]/div/form/div/div[1]/div/div/div/input')
username.send_keys('18734573096')

time.sleep(3)

username = driver.find_element(By.XPATH, '//*[@id="root"]/div/form/div/div[2]/div/div/div/input')
username.send_keys('Runningwei..6')

time.sleep(2)
driver.find_element(By.XPATH, '//*[@id="root"]/div/form/div/div[3]/span').click()
time.sleep(5)

# 必须加上表单退出，否者就是死元素无法定位
driver.switch_to.default_content()

# 这行代码的作用就相当于你手动点了一下‘刑事案件’那个按钮
# 要下载民事案件就把下一行代码里的刑事案件改成‘民事案件’，以此类推
driver.find_element(By.LINK_TEXT, '刑事案件').click()

time.sleep(10)
# testHtml(driver.page_source)

_lastWindow = driver.window_handles[-1]
driver.switch_to.window(_lastWindow)


# 按照裁判日期排序显示最新600条
# driver.find_element(By.LINK_TEXT, '裁判日期').click()

# 按照审判程序排序显示最新600条
# driver.find_element(By.LINK_TEXT, '审判程序').click()


# 按照法院层级、地域及法院进行检索
driver.find_element(By.LINK_TEXT, '上海市(243429)').click()
time.sleep(3)

driver.find_element(By.LINK_TEXT, '基层法院(201257)').click()


# 在搜索框输入关键词进行高级检索
# 定位到搜索框
# keyword = driver.find_element(By.XPATH, '//*[@id="_view_1545034775000"]/div/div[1]/div[2]/input')
# time.sleep(3)
# 输入高级检索关键词，例如 工程纠纷
# keyword.send_keys('工程纠纷')
time.sleep(3)
# 点击搜索按钮
driver.find_element(By.XPATH, '//*[@id="_view_1545034775000"]/div/div[1]/div[3]').click()

# 将每页文件数设置为最大,15条
page_size_box = Select(driver.find_element(By.XPATH, '//*[@id="_view_1545184311000"]/div[8]/div/select'))
page_size_box.select_by_visible_text('15')

def test_exceptions(xpath):
    try:
        driver.find_element(By.XPATH, xpath)
        return True
    except:
        return False

page = 1
# 最多显示600条文件,也就是40页
while page <= 40:
    time.sleep(5+page/10)
    for i in range(15):
        time.sleep(5+i/10)
        event_xpath = '//*[@id="_view_1545184311000"]/div[' + str(i+3) + ']/div[6]/div/a[2]'
        if test_exceptions(event_xpath) == True:
            try:
                driver.find_element(By.XPATH, event_xpath).click()
            except:
                print("Error: " + event_xpath)

        else:
            event_xpath = '//*[@id="_view_1545184311000"]/div[' + str(i+3) + ']/div[5]/div/a[2]'
            if test_exceptions(event_xpath) == True:
                driver.find_element(By.XPATH, event_xpath).click()

    # 下一页按钮,不能用Xpath定位,因为“下一页”按钮位置不固定
    # driver.find_element(By.LINK_TEXT, '下一页').click()
    time.sleep(10)
    driver.find_element(By.LINK_TEXT, '下一页').click()
    # 必须加上表单退出，否者就是死元素无法定位
    driver.switch_to.default_content()
    page += 1

# 关闭整个浏览器窗口并终止与浏览器的会话
driver.quit()

