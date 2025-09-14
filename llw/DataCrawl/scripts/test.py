from doc_reader import DocReader

try:
    # 仅获取HTML内容
    html_content = DocReader.convert_to_html("D:\\文件下载\\微信下载\\WeChat Files\\wxid_bs8c25j6yp0q22\FileStorage\\File\\2025-01\民事判决书\\1c45ddd9577fefc4230032d19fc5c9b8.docx")
    print(html_content)
    
    # 或者直接保存为HTML文件
    # output_path = DocReader.save_as_html("example.docx", "output.html")
    # print(f"文件已保存到：{output_path}")
    
except Exception as e:
    print(f"处理失败：{e}")