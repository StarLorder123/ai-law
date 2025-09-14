package com.law.rag;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import org.apache.pdfbox.text.PDFTextStripper;

@SpringBootTest
public class DocumentTests {

    // public static String readWord(InputStream is ,boolean doc) {
    //     String buffer = "";
    //     try {
    //         if (doc) {
    //           //根据文件后缀进判断分支，.doc和.docx使用的方法不一样
    //             BodyContentHandler handler = new BodyContentHandler(1024 * 1024 * 10);//设置文档大小，避免文件太大tika报错，默认大小就是1024*1024*10
    //             Metadata metadata = new Metadata();
    //             ParseContext pContext = new ParseContext();
    //             Parser msOfficeParser = new OfficeParser();
    //             msOfficeParser.parse(is , handler , metadata , pContext);
    //             buffer = handler.toString();
    //             is.close();
    //         } else {
    //             Tika tika = new Tika();
    //             buffer = tika.parseToString(is);
    //             is.close();
    //         }
    //     } catch (Exception e) {
    //         throw new RRException("读取文件失败，请用Microsoft Word另存为之后再上传");
    //     }

    //     return buffer;
    // }

    @Test
    public void TestTiTaxDocument() {
        Tika tika = new Tika();
        try {
            // 文件路径
            File file = new File("E:\\李锦浩\\00000004-简历\\简历-李锦浩-20240602-中文.pdf");
            // 提取文本
            String text = tika.parseToString(file);
            System.out.println("Extracted text: ");
            System.out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void TestTiTaxDocument2() throws IOException, TikaException {
        String pdfFilePath = "D:\\文件下载\\微信下载\\WeChat Files\\wxid_bs8c25j6yp0q22\\FileStorage\\File\\2024-02\\基于镜像法的L型矿井巷道电磁波反射场强预测_于洋.pdf"; // PDF 文件路径
        String outputDir = "E:\\Code\\llw\\llw-server\\rag\\test";       // 输出目录

        try {
            // Step 1: 提取 PDF 文本
            extractPdfText(pdfFilePath);

            // Step 2: 提取 PDF 中的图片
            extractPdfImages(pdfFilePath, outputDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestTiTaxDocument3() throws IOException, TikaException {
        String pdfFilePath = "D:\\文件下载\\微信下载\\WeChat Files\\wxid_bs8c25j6yp0q22\\FileStorage\\File\\2024-02\\基于镜像法的L型矿井巷道电磁波反射场强预测_于洋.pdf"; // PDF 文件路径
        String outputDir = "E:\\Code\\llw\\llw-server\\rag\\test";       // 输出目录

        try {
            extractTextWithImagePositions(pdfFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用 Tika 提取 PDF 的文本内容
     */
    private void extractPdfText(String pdfFilePath) throws IOException, TikaException {
        Tika tika = new Tika();
        String text = tika.parseToString(new File(pdfFilePath));
        System.out.println("Extracted Text:");
        System.out.println(text);
    }

    /**
     * 使用 PDFBox 提取 PDF 中的图片并保存到本地
     */
    private void extractPdfImages(String pdfFilePath, String outputDir) throws IOException {
        // 加载 PDF 文档
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDPageTree pages = document.getPages(); // 使用 PDPageTree 类型
            int imageCount = 0;

            for (PDPage page : pages) { // 直接迭代
                Iterable<COSName> xObjects = page.getResources().getXObjectNames();
                for (COSName name : xObjects) {
                    if (page.getResources().isImageXObject(name)) {
                        PDImageXObject imageObject = (PDImageXObject) page.getResources().getXObject(name);

                        // 提取图片
                        BufferedImage image = imageObject.getImage();

                        // 保存图片到本地
                        String outputFileName = outputDir + "/image_" + (++imageCount) + ".png";
                        File outputFile = new File(outputFileName);
                        ImageIO.write(image, "PNG", outputFile);
                        System.out.println("Saved image: " + outputFileName);
                    }
                }
            }

            System.out.println("Total images extracted: " + imageCount);
        }
    }

    /**
     * 提取 PDF 文本，并在图片所在位置插入标记
     */
    private void extractTextWithImagePositions(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            StringBuilder finalTextWithImages = new StringBuilder();

            int pageIndex = 0;
            for (PDPage page : document.getPages()) {
                pageIndex++;
                // 提取该页的文本
                pdfTextStripper.setStartPage(pageIndex);
                pdfTextStripper.setEndPage(pageIndex);
                String pageText = pdfTextStripper.getText(document);

                // 查找该页的图片
                int imageIndex = 0;
                for (var name : page.getResources().getXObjectNames()) {
                    if (page.getResources().isImageXObject(name)) {
                        PDImageXObject imageObject = (PDImageXObject) page.getResources().getXObject(name);

                        // 假设我们无法获得精确坐标，仅插入一个标记
                        imageIndex++;
                        String imageMarker = String.format("[IMAGE_%d_PAGE_%d]\n", imageIndex, pageIndex);
                        pageText += imageMarker; // 将图片标记添加到文本末尾
                    }
                }

                // 将该页内容（包含图片标记）添加到总文本
                finalTextWithImages.append(pageText).append("\n");
            }

            // 输出带图片位置信息的文本
            System.out.println(finalTextWithImages.toString());
        }
    }
}
