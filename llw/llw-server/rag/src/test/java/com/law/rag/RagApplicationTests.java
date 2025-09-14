package com.law.rag;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.law.rag.service.ChatCollectionService;
import com.law.rag.util.SpringContextHolder;
import com.law.rag.vo.CaseAFileVo;
import com.law.rag.entity.CaseCollectionEntity;
import com.law.rag.mapper.CaseCollectionMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.law.rag.config.OllamaConfig;
import com.law.rag.entity.TestEntity;
import com.law.rag.mapper.TestMapper;
import com.law.rag.service.LLMModelService;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import static dev.langchain4j.data.message.UserMessage.userMessage;

@SpringBootTest
class RagApplicationTests {

    @Autowired
    private OllamaConfig ollamaConfig;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private CaseCollectionMapper caseCollectionMapper;

    @Autowired
    private LLMModelService llmModelService;

    @Test
    void TestQueryTestDataByID() {
        TestEntity entity = testMapper.selectById(1);
        System.out.println("This is testQueryTestDataByID:" + entity.toString());
    }

    @Test
    void TestQueryCaseCollectionDataByID() {
        CaseCollectionEntity caseCollectionEntity = caseCollectionMapper.selectById("123456");
        System.out.println(caseCollectionEntity.toString());
    }

    @Test
    void TestQueryCaseWithFile() {
        CaseAFileVo caseAFileVo = caseCollectionMapper.getCaseCollectionWithFile("123456");
        System.out.println(caseAFileVo.toString());
    }

    @Test
    void TestLLMStreamModelChat() throws InterruptedException {
        String value = "Write a 100-word poem about Java and AI";
        System.out.println("[User]: " + value);
        // llmModelService.streamChat(value);

        Thread.sleep(10 * 1000);
    }

    @Test
    void simple_example() {

        System.out.println("This is simple_example function");

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://192.168.23.11:11434/")
                .modelName("llama3.2-vision")
                .build();

        String answer = model.generate("Write a 100-word poem about Java and AI");

        System.out.println(answer);

        String answer1 = model.generate("What did I ask u jsut now?");

        System.out.println(answer1);
    }

    static class Calculator {

        @Tool("Calculates the length of a string")
        int stringLength(String s) {
            System.out.println("Called stringLength() with s='" + s + "'");


            return s.length();
        }

        @Tool("Calculates the sum of two numbers")
        int add(int a, int b) {
            System.out.println("Called add() with a=" + a + ", b=" + b);
            return a + b;
        }

        @Tool("Calculates the square root of a number")
        double sqrt(int x) {
            System.out.println("Called sqrt() with x=" + x);
            return Math.sqrt(x);
        }

        @Tool("Add Email")
        String addEmail(String name, int num) {
            System.out.println("Called addEmail() with name='" + name + "'");
            TestEntity testEntity = new TestEntity();
            testEntity.setId(num);
            testEntity.setUsername(name);

            TestMapper testMapper = SpringContextHolder.getBean(TestMapper.class);
            testMapper.insert(testEntity);
            return "Add Email Successfully";
        }

        @Tool("发邮件")
        String sendEmail(String email, String content) {
            System.out.println("Called sendEmail() with email='" + email + "', content='" + content + "'");
            return "Search E-mail address first, then do send e-mail";
        }

        @Tool("Search Email address")
        String searchEmail(String name, int id) {
            TestMapper testMapper = SpringContextHolder.getBean(TestMapper.class);
            TestEntity testEntity = testMapper.selectById(id);
            System.out.println("Called searchEmail() with name='" + testEntity.getUsername() + "'");
            return "查询到的邮箱为：" + testEntity.getUsername() + "@163.com";
        }

        @Tool("Do send Email")
        String doSendEmail(String email) {
            System.out.println("Called doSendEmail() with email='" + email);
            return "send successfully";
        }
    }

    interface Assistant {

        String chat(String userMessage);
    }

    @Test
    void simple_tool_example() {

        System.out.println("This is simple_tool_example function");

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(String.format("http://%s:%d", ollamaConfig.getHost(), ollamaConfig.getPort()))
//                .modelName("llama3.1:8b")
                .modelName("qwen2.5:7b")
                .temperature(0.2)
                .build();

//        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
//                .baseUrl(String.format("http://%s:%d", ollamaConfig.getHost(), ollamaConfig.getPort()))
//                .modelName("qwen2.5:7b")
//                .temperature(0.2)
//                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
//                .streamingChatLanguageModel(model)
                .tools(new Calculator())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

//        TokenStream tokenStream = assistant.chat("给编号30的人发送一条Helloworld的邮件");
//
//        tokenStream.onNext((String token) -> System.out.println(token))
////                .onRetrieved((List<Content> contents) -> System.out.println(contents))
//                .onToolExecuted((ToolExecution toolExecution) -> System.out.println(toolExecution))
//                .onComplete((Response<AiMessage> response) -> System.out.println(response))
//                .onError((Throwable error) -> error.printStackTrace())
//                .start();

        String question = "给编号30的人发送一条Helloworld的邮件";

        String answer = assistant.chat(question);

        System.out.println(answer);
    }

    @Test
    void streaming_example() {

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                // .baseUrl(String.format("http://%s:%d", ollama.getHost(),
                // ollama.getMappedPort(PORT)))
                .baseUrl(String.format("http://%s:%d", ollamaConfig.getHost(), ollamaConfig.getPort()))
                .modelName(ollamaConfig.getName())
                .temperature(0.0)
                .build();

        String userMessage = "Write a 100-word poem about Java and AI";

        CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();
        model.generate(userMessage, new StreamingResponseHandler<AiMessage>() {

            @Override
            public void onNext(String token) {
                // System.out.print(token);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                futureResponse.complete(response);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });

        System.out.println("finally:" + futureResponse.join().content().text());
    }

    @Test
    public void testStream_Example() {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("llama3.2-vision")
                .build();

        List<Content> contents = new ArrayList<>();
        contents.add(new ImageContent(Paths.get("E:\\图片\\wallhaven-4v2rp8.jpg").toUri()));
        contents.add(new TextContent("这个图片描述了什么？"));

        ChatMessage chatMessage = new UserMessage(contents);

        Response<AiMessage> answer = model.generate(chatMessage);

        System.out.println(answer.content().text());
    }

    @Test
    public void testStreaming_example1() {
        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl(String.format("http://%s:%d", ollamaConfig.getHost(), ollamaConfig.getPort()))
                .modelName(ollamaConfig.getName())
                .temperature(0.0)
                .build();

        String userMessage = "请帮我写一首七言绝句";

        CompletableFuture<Response<AiMessage>> futureResponse = new CompletableFuture<>();

        // model.generate(userMessage, new LlwStreamingResponseHandler());

        System.out.println("finally: " + futureResponse.join().content().text());
    }

    // // 用于统计输出的单词数量
    // final int WORD_THRESHOLD = 10; // 每当输出5个单词时触发一次
    // final StringBuilder currentOutput = new StringBuilder();
    // int wordCount = 0;

    @Test
    public void testStreaming_example() {
        System.out.println("Default Charset: " + Charset.defaultCharset());

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl(String.format("http://%s:%d", ollamaConfig.getHost(), ollamaConfig.getPort()))
                .modelName(ollamaConfig.getName())
                .temperature(0.0)
                .build();

        String userMessage = "请帮我写一首七言绝句";

        // CompletableFuture<Response<AiMessage>> futureResponse = new
        // CompletableFuture<>();

        model.generate(userMessage, new StreamingResponseHandler<AiMessage>() {

            // 用于统计输出的单词数量
            final int WORD_THRESHOLD = 10; // 每当输出5个单词时触发一次
            final StringBuilder currentOutput = new StringBuilder();
            int wordCount = 0;

            @Override
            public void onNext(String token) {

                // 将token添加到输出中
                currentOutput.append(token);

                // 计算当前token中的单词数量
                String[] tokens = token.split("\\s+");
                wordCount += tokens.length;

                // 每当输出的单词数达到5个时，触发一次输出
                if (wordCount >= WORD_THRESHOLD) {
                    System.out.println("Output so far: " + currentOutput.toString().trim());
                    wordCount = 0; // 重置单词计数器
                    currentOutput.setLength(0); // 重置输出内容
                }
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                // 完成后输出最终内容
                // futureResponse.complete(response);
                System.out.println("Final output: " + response.content().text());
            }

            @Override
            public void onError(Throwable error) {
                // futureResponse.completeExceptionally(error);
            }
        });

        // System.out.println("finally: " + futureResponse.join().content().text());
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testOllamaMemory() throws Exception, ExecutionException {
        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                // .baseUrl(String.format("http://%s:%d", ollama.getHost(),
                // ollama.getMappedPort(PORT)))
                .baseUrl(String.format("http://%s:%d", "localhost", 11434))
                .modelName("deepseek-r1:8b")
//                .modelName("qwen2.5:7b")
                .temperature(0.0)
                .topK(3)
                .build();
        ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(128000, new OpenAiTokenizer());

        SystemMessage systemMessage = SystemMessage.from(
                "你是一名律师助理。需要根据法律法规和给出的案件信息为律师给出对应的建议。\n" +
                        "现提出如下要求：\n" +
                        "1. 建议需要贴近提供的事实\n" +
                        "2. 法律条款引用恰当，引用的法律条款必须在文书中明确提及。" +
                        "3. 对材料中涉及的人称输出准确，且前后保持一致");
        chatMemory.add(systemMessage);

        UserMessage userMessage1 = userMessage(
                "这是一篇判决书：北京市海淀区人民法院\n" +
                        "民 事 判 决 书\n" +
                        "(2022)京0108民初6078号\n" +
                        "原告：爱尔国际防护服装科学研究院（北京）有限公司，住所地北京市海淀区。\n" +
                        "法定代表人：杨朝鹏，执行董事。\n" +
                        "委托诉讼代理人：杨娟娟，女，该公司员工。\n" +
                        "被告：钟为，男，1969年6月7日出生，住址及户籍所在地均系广东省。\n" +
                        "原告爱尔国际防护服装科学研究院（北京）有限公司（以下简称爱尔国际公司）与被告钟为劳动争议纠纷一案，本院于2022年2月9日立案后，依法适用普通程序，公开开庭进行了审理。原告爱尔国际公司之委托诉讼代理人杨娟娟及被告钟为到庭参加了诉讼，本案现已审理终结。\n" +
                        "爱尔国际公司向本院提出诉讼请求：1、判令我公司无需支付钟为2021年6月11日至2021年7月8日工资12689.66元；2、判令我公司无需支付钟为违法解除劳动合同赔偿金13800元。事实和理由：钟为编造博士学历、中国高级会计师职称，谎称自己有大量投资资源，可以为我公司融资1000万元至5亿元人民币，于2021年6月11日开始为我公司提供融资服务，双方约定钟为一个月内先为公司融资1000万元，购买我公司原始股30万股，我公司支付其生活费5000元，另为其支付融资提成100万元，并聘请钟为担任总裁一职，后发现钟为手中并无投资资源，纯属欺骗，我公司于2021年7月8日将钟为辞退，仲裁裁决认定的事实及适用的法律有误，我公司不服仲裁裁决结果，向法院提起诉讼。\n" +
                        "钟为辩称，我不同意爱尔国际公司的诉讼请求。\n" +
                        "当事人围绕诉讼请求依法提交了证据，本院组织当事人进行了证据交换和质证。对当事人无异议的证据，本院予以确认并在卷佐证。钟为于2021年6月11日开始为爱尔国际公司从事融资工作，2021年7月8日爱尔国际公司将钟为辞退。\n" +
                        "钟为主张其与爱尔国际公司之间系劳动关系，其月工资按照总裁待遇执行，月工资标准为1.38万元，另有6万元分红，但爱尔国际公司未告知分红的标准，爱尔国际公司未向其发放过工资，并于2021年7月8日将其违法辞退。\n" +
                        "爱尔国际公司则主张双方是劳务关系，钟为所述的工资标准是在完成融资1000万元之后才执行的，未完成融资任务则月工资为5000元，2021年7月8日公司将钟为辞退，理由为钟为没有完成融资任务，工作能力不足，且存在学历、职称造假，后爱尔国际公司更正辞退原因为钟为未提供上一家单位的离职证明、不能胜任本职工作、工作态度差给公司造成损失。另查，在仲裁阶段钟为主张2021年7月8日爱尔国际公司以其未融资到1000万为由电话通知将其辞退，爱尔国际公司认可2021年7月8日以钟为融资未成功为由将其辞退。\n" +
                        "爱尔国际公司向本院提交了以下证据。\n" +
                        "一、钟为简历，钟为不认可该证据的真实性及证明目的。\n" +
                        "二、爱尔国际公司员工与钟为的微信记录。其中显示钟为说“投资人都是我多年的朋友”，爱尔国际公司员工说：“自2021.6.11开始算兼职，发补贴生活费第一个月5000第二个月5000第三个月5000提成办法：无抵押、股权融资1000万元，融资到账按实际融资金额3%提成；另发8800元奖金”。钟为回复：“第一个月不是兼职、第二个月才开始兼职，所以第一个月：13800+社保”。钟为认可该证据的真实性，不认可其证明目的。\n" +
                        "三、辞退通知书，内容为：“钟为，你于2021年6月11日到我公司报道以来，拒不提供原单位离职证明和相关证件，对外谎称你是朋友介绍来我单位工作的并公司发现你不能胜任本职工作，劳动态度差，由于你的不良行为，还给公司造成了经济损失，本公司决定将你辞退，终止与你的劳动关系......”。钟为认可该证据的真实性，不认可其中的辞退理由。\n" +
                        "钟为向本院提交了以下证据。\n" +
                        "一、钟为与爱尔国际公司员工的微信记录，其中显示有钟为到公司报道及爱尔国际公司为钟为订购车票等内容。爱尔国际公司认可该证据的真实性。\n" +
                        "二、学位证书、照片及证书，钟为据此主张其简历属实。爱尔国际公司认可学位证书的真实性认可，不认可其他证据的真实性，主张钟为的学位是非全日制的。\n" +
                        "钟为以要求爱尔国际公司支付工资、未签劳动合同二倍工资差额、违法解除劳动关系赔偿金为由向北京市海淀区劳动人事争议仲裁委员会提起申请，该委出具京海劳人仲字[2021]第16496号裁决书，裁决如下：一、爱尔国际公司支付钟为2021年6月11日至2021年7月8日工资12689.66元；二、爱尔国际公司支付钟为违法解除劳动关系赔偿金13800元；三、驳回钟为的其他仲裁请求。爱尔国际公司不服裁决结果，于法定期限内向本院起诉。\n" +
                        "本院认为，关于双方法律关系的性质，钟为与爱尔国际公司均符合建立劳动关系的主体要求，且钟为从事爱尔国际公司安排的融资工作，爱尔国际公司与钟为约定按月发放劳动报酬，且爱尔国际公司向钟为发送的辞退通知书中也体现有与钟为终止劳动关系的内容，故本院对钟为所持双方系劳动关系之主张予以采信。\n" +
                        "爱尔国际公司作为用工管理一方应就钟为的工资标准、工资发放情况及劳动关系解除情况负有举证责任。现爱尔国际公司虽主张钟为所述的工资标准需以融资完成为条件，但并未就此进行充分举证，本院对其公司之主张不予采信。爱尔国际公司应支付钟为2021年6月11日至2021年7月8日工资12689.66元。爱尔国际公司向钟为送达有书面解除通知，本院以该通知中所载明的解除理由进行审查。爱尔国际公司未就钟为存在辞退通知书中的辞退事由进行充分举证，也未证明上述行为可予以解除劳动关系有相关制度依据，故爱尔国际公司与钟为解除劳动关系确有不当，系违法解除。爱尔国际公司应支付钟为违法解除劳动关系赔偿金13800元。\n" +
                        "综上所述，依据《中华人民共和国劳动合同法》第三十条及第四十八条之规定，判决如下：\n" +
                        "一、爱尔国际防护服装科学研究院（北京）有限公司于本判决生效之日起七日内向钟为支付2021年6月11日至2021年7月8日工资12689.66元；\n" +
                        "二、爱尔国际防护服装科学研究院（北京）有限公司于本判决生效之日起七日内向钟为支付违法解除劳动关系赔偿金13800元。\n" +
                        "如果未按本判决指定的期间履行给付金钱义务，应当依照《中华人民共和国民事诉讼法》第二百六十条之规定，加倍支付迟延履行期间的债务利息。\n" +
                        "案件受理费十元，由爱尔国际防护服装科学研究院（北京）有限公司负担。\n" +
                        "如不服本判决，可在判决书送达之日起十五日内，向本院递交上诉状，并按对方当事人的人数提出副本，上诉于北京市第一中级人民法院。\n" +
                        "审判员　　孟雅慧\n" +
                        "二〇二三年五月二十六日\n" +
                        "书记员　　郭家伟\n" +
                        "根据案件类型可以分为：一审民事案件、二审民事案件、一审刑事案件、二审刑事案件、一审行政案件、二审行政案件\n" +
                        "请根据判决文书，说明一下该案件类型，简要描述案件的基本案情，案件中涉及到的所有当事人以及其关系、法院对案件的判断，以及最终判决。");
        chatMemory.add(userMessage1);

        System.out.println("[User]: " + userMessage1.text());
        System.out.print("[LLM]: ");

        CompletableFuture<AiMessage> futureAiMessage = new CompletableFuture<>();

        StreamingResponseHandler<AiMessage> handler = new StreamingResponseHandler<AiMessage>() {

            // 用于统计输出的单词数量
            // final int WORD_THRESHOLD = 10; // 每当输出5个单词时触发一次
            // final StringBuilder currentOutput = new StringBuilder();
            // int wordCount = 0;

            @Override
            public void onNext(String token) {
                System.out.print(token);
                // 将token添加到输出中
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                futureAiMessage.complete(response.content());
                // System.out.println("Final output: " + response.content().text());
            }

            @Override
            public void onError(Throwable throwable) {
            }
        };

        model.generate(chatMemory.messages(), handler);

        chatMemory.add(futureAiMessage.get());

//        UserMessage userMessage2 = userMessage(
//                "请根据文书，详细描述一下案件的过程");
//        chatMemory.add(userMessage2);
//
//        System.out.println("\n\n[User]: " + userMessage2.text());
//        System.out.print("[LLM]: ");
//
//        model.generate(chatMemory.messages(), handler);
//        chatMemory.add(futureAiMessage.get());


//        UserMessage userMessage3 = userMessage(
//                "请详细描述一下转账的详细过程。");
//        chatMemory.add(userMessage3);
        UserMessage userMessage3 = userMessage(
                "请详细说明一下案件的纠纷起因和过程");
        chatMemory.add(userMessage3);

        System.out.println("\n\n[User]: " + userMessage3.text());
        System.out.print("[LLM]: ");

        model.generate(chatMemory.messages(), handler);

        Thread.sleep(20 * 1000);
    }

    @Test
    public void StreamAiServiceToken() throws InterruptedException {

        interface Assistant {

            TokenStream chat(String message);
        }

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                // .baseUrl(String.format("http://%s:%d", ollama.getHost(),
                // ollama.getMappedPort(PORT)))
                .baseUrl(String.format("http://%s:%d", "localhost", 11434))
//                .modelName("deepseek-r1:8b")
                .modelName("qwen2.5:7b")
                .temperature(0.0)
                .topK(3)
                .build();

        Assistant assistant = AiServices.create(Assistant.class, model);

        TokenStream tokenStream = assistant.chat("Tell me a joke");

        tokenStream.onNext((String token) -> System.out.println(token))
//                .onRetrieved((List<Content> contents) -> System.out.println(contents))
                .onToolExecuted((ToolExecution toolExecution) -> System.out.println(toolExecution))
                .onComplete((Response<AiMessage> response) -> System.out.println(response))
                .onError((Throwable error) -> error.printStackTrace())
                .start();

        Thread.sleep(20 * 1000);
    }
}
