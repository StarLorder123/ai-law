import React from "react";
import MarkdownIt from "markdown-it";

const MarkdownRenderer = ({ markdownText }) => {
  // 初始化 markdown-it 实例
  const md = new MarkdownIt();
  
  // 转换 Markdown 为 HTML
  const renderedHTML = md.render(markdownText);

  return (
    <div
      dangerouslySetInnerHTML={{ __html: renderedHTML }}
      style={{ padding: "1rem", background: "#f9f9f9" }}
    />
  );
};

export default MarkdownRenderer;
