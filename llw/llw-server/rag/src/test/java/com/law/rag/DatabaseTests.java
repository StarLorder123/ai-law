package com.law.rag;

import com.law.rag.entity.ChatBaseCollectionEntity;
import com.law.rag.entity.FileEntity;
import com.law.rag.entity.User;
import com.law.rag.mapper.CaseBaseCollectionMapper;
import com.law.rag.mapper.ChatBaseCollectionMapper;
import com.law.rag.mapper.FileMapper;
import com.law.rag.util.Neo4jUtil;
import com.law.rag.util.UUIDUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallzh.BgeSmallZhEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.neo4j.Neo4jContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.store.embedding.neo4j.Neo4jEmbeddingStore;
import dev.langchain4j.store.graph.neo4j.Neo4jGraph;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DatabaseTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CaseBaseCollectionMapper caseBaseCollectionMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private ChatBaseCollectionMapper chatBaseCollectionMapper;

    @Test
    public void testCaseBaseCollectionQueryAll(){
        System.out.println(caseBaseCollectionMapper.selectList(null));
    }

    @Test
    public void testMongoDB() {
        User user = new User();
        user.setId("2");
        user.setAge(2);
        user.setName("hello world1");

        mongoTemplate.save(user);

        System.out.println(mongoTemplate.findAll(User.class));
    }

    @Test
    public void testFileMapperInsert() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileid(UUIDUtil.generateUUIDWithoutHyphens());
        fileEntity.setFilename("Hello");
        fileEntity.setPath(UUIDUtil.generateUUIDWithoutHyphens());
        fileEntity.setSize(100L);
        fileEntity.setSum("xxxx");

        fileMapper.insert(fileEntity);
    }


    @Test
    public void testChatBaseCollectionInsert(){
        ChatBaseCollectionEntity chatBaseCollectionEntity=new ChatBaseCollectionEntity();
        chatBaseCollectionEntity.setMemoryid("test");
        chatBaseCollectionEntity.setContent("Hello World");
        chatBaseCollectionEntity.setRole("USER");
        chatBaseCollectionMapper.insert(chatBaseCollectionEntity);
    }

    @Test
    public void testFileMapperQuery() {
        System.out.println(
                fileMapper.findByFileId("c2605ebf1e5141b6800f21f439c60206")
        );
    }

    @Test
    public void testFileMapperQueryList() {

        List<String> list = new ArrayList<>();

        list.add("1f1dc23d8990497d9335bd0379f26176");
        list.add("c2605ebf1e5141b6800f21f439c60206");

        System.out.println(fileMapper.getFilesByIds(list));
    }

    @Test
    public void testMilvusDatabase() {
        EmbeddingStore<TextSegment> embeddingStore = MilvusEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(19530)
                .collectionName("test_law_collection")
                .dimension(512)
                .build();

//        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingModel embeddingModel = new BgeSmallZhEmbeddingModel();

//        TextSegment segment1 = TextSegment.from("　　第二十五条　共同犯罪是指二人以上共同故意犯罪。\n" +
//                "　　二人以上共同过失犯罪，不以共同犯罪论处；应当负刑事责任的，按照他们所犯的罪分别处罚。\n");
//        Embedding embedding1 = embeddingModel.embed(segment1).content();
//        embeddingStore.add(embedding1, segment1);
//
//        TextSegment segment2 = TextSegment.from(                "　　第二十六条　组织、领导犯罪集团进行犯罪活动的或者在共同犯罪中起主要作用的，是主犯。\n" +
//                "　　三人以上为共同实施犯罪而组成的较为固定的犯罪组织，是犯罪集团。\n"+                "　　对组织、领导犯罪集团的首要分子，按照集团所犯的全部罪行处罚。\n" +
//                "　　对于第三款规定以外的主犯，应当按照其所参与的或者组织、指挥的全部犯罪处罚。\n"  );
//        Embedding embedding2 = embeddingModel.embed(segment2).content();
//        embeddingStore.add(embedding2, segment2);
//
//        TextSegment segment3 = TextSegment.from("　　第二十二条　为了犯罪，准备工具、制造条件的，是犯罪预备。\n" +
//                "　　对于预备犯，可以比照既遂犯从轻、减轻处罚或者免除处罚。\n" +
//                "　　第二十三条　已经着手实行犯罪，由于犯罪分子意志以外的原因而未得逞的，是犯罪未遂。\n" +
//                "　　对于未遂犯，可以比照既遂犯从轻或者减轻处罚。\n" +
//                "　　第二十四条　在犯罪过程中，自动放弃犯罪或者自动有效地防止犯罪结果发生的，是犯罪中止。\n" +
//                "　　对于中止犯，没有造成损害的，应当免除处罚；造成损害的，应当减轻处罚。\n");
//        Embedding embedding3 = embeddingModel.embed(segment2).content();
//        embeddingStore.add(embedding3, segment3);
//
//        TextSegment segment4 = TextSegment.from("第一百零二条　勾结外国，危害中华人民共和国的主权、领土完整和安全的，处无期徒刑或者十年以上有期徒刑。\n" +
//                "　　与境外机构、组织、个人相勾结，犯前款罪的，依照前款的规定处罚。\n" +
//                "　　第一百零三条　组织、策划、实施分裂国家、破坏国家统一的，对首要分子或者罪行重大的，处无期徒刑或者十年以上有期徒刑；对积极参加的，处三年以上十年以下有期徒刑；对其他参加的，处三年以下有期徒刑、拘役、管制或者剥夺政治权利。\n" +
//                "　　煽动分裂国家、破坏国家统一的，处五年以下有期徒刑、拘役、管制或者剥夺政治权利；首要分子或者罪行重大的，处五年以上有期徒刑。\n" +
//                "　　第一百零四条　组织、策划、实施武装叛乱或者武装暴乱的，对首要分子或者罪行重大的，处无期徒刑或者十年以上有期徒刑；对积极参加的，处三年以上十年以下有期徒刑；对其他参加的，处三年以下有期徒刑、拘役、管制或者剥夺政治权利。\n" +
//                "　　策动、胁迫、勾引、收买国家机关工作人员、武装部队人员、人民警察、民兵进行武装叛乱或者武装暴乱的，依照前款的规定从重处罚。\n" +
//                "　　第一百零五条　组织、策划、实施颠覆国家政权、推翻社会主义制度的，对首要分子或者罪行重大的，处无期徒刑或者十年以上有期徒刑；对积极参加的，处三年以上十年以下有期徒刑；对其他参加的，处三年以下有期徒刑、拘役、管制或者剥夺政治权利。\n" +
//                "　　以造谣、诽谤或者其他方式煽动颠覆国家政权、推翻社会主义制度的，处五年以下有期徒刑、拘役、管制或者剥夺政治权利；首要分子或者罪行重大的，处五年以上有期徒刑。\n" +
//                "　　第一百零六条　与境外机构、组织、个人相勾结，实施本章第一百零三条、第一百零四条、第一百零五条规定之罪的，依照各该条的规定从重处罚。\n" +
//                "　　第一百零七条　境内外机构、组织或者个人资助境内组织或者个人实施本章第一百零二条、第一百零三条、第一百零四条、第一百零五条规定之罪的，对直接责任人员，处五年以下有期徒刑、拘役、管制或者剥夺政治权利；情节严重的，处五年以上有期徒刑。\n" +
//                "　　第一百零八条　投敌叛变的，处三年以上十年以下有期徒刑；情节严重或者带领武装部队人员、人民警察、民兵投敌叛变的，处十年以上有期徒刑或者无期徒刑。\n" +
//                "　　第一百零九条　国家机关工作人员在履行公务期间，擅离岗位，叛逃境外或者在境外叛逃，危害中华人民共和国国家安全的，处五年以下有期徒刑、拘役、管制或者剥夺政治权利；情节严重的，处五年以上十年以下有期徒刑。\n" +
//                "　　掌握国家秘密的国家工作人员犯前款罪的，依照前款的规定从重处罚。\n" +
//                "　　第一百一十条　有下列间谍行为之一，危害国家安全的，处十年以上有期徒刑或者无期徒刑；情节较轻的，处三年以上十年以下有期徒刑：\n" +
//                "　　（一）参加间谍组织或者接受间谍组织及其代理人的任务的；\n" +
//                "　　（二）为敌人指示轰击目标的。\n" +
//                "　　第一百一十一条　为境外的机构、组织、人员窃取、刺探、收买、非法提供国家秘密或者情报的，处五年以上十年以下有期徒刑；情节特别严重的，处十年以上有期徒刑或者无期徒刑；情节较轻的，处五年以下有期徒刑、拘役、管制或者剥夺政治权利。\n" +
//                "　　第一百一十二条　战时供给敌人武器装备、军用物资资敌的，处十年以上有期徒刑或者无期徒刑；情节较轻的，处三年以上十年以下有期徒刑。\n" +
//                "　　第一百一十三条　本章上述危害国家安全罪行中，除第一百零三条第二款、第一百零五条、第一百零七条、第一百零九条外，对国家和人民危害特别严重、情节特别恶劣的，可以判处死刑。\n" +
//                "　　犯本章之罪的，可以并处没收财产。\n");
//        Embedding embedding4 = embeddingModel.embed(segment2).content();
//        embeddingStore.add(embedding4, segment4);
//
//        TextSegment segment5 = TextSegment.from("第一百一十四条　放火、决水、爆炸、投毒或者以其他危险方法破坏工厂、矿场、油田、港口、河流、水源、仓库、住宅、森林、农场、谷场、牧场、重要管道、公共建筑物或者其他公私财产，危害公共安全，尚未造成严重后果的，处三年以上十年以下有期徒刑。\n" +
//                "　　第一百一十五条　放火、决水、爆炸、投毒或者以其他危险方法致人重伤、死亡或者使公私财产遭受重大损失的，处十年以上有期徒刑、无期徒刑或者死刑。\n" +
//                "　　过失犯前款罪的，处三年以上七年以下有期徒刑；情节较轻的，处三年以下有期徒刑或者拘役。\n" +
//                "　　第一百一十六条　破坏火车、汽车、电车、船只、航空器，足以使火车、汽车、电车、船只、航空器发生倾覆、毁坏危险，尚未造成严重后果的，处三年以上十年以下有期徒刑。\n" +
//                "　　第一百一十七条　破坏轨道、桥梁、隧道、公路、机场、航道、灯塔、标志或者进行其他破坏活动，足以使火车、汽车、电车、船只、航空器发生倾覆、毁坏危险，尚未造成严重后果的，处三年以上十年以下有期徒刑。\n" +
//                "　　第一百一十八条　破坏电力、燃气或者其他易燃易爆设备，危害公共安全，尚未造成严重后果的，处三年以上十年以下有期徒刑。\n" +
//                "　　第一百一十九条　破坏交通工具、交通设施、电力设备、燃气设备、易燃易爆设备，造成严重后果的，处十年以上有期徒刑、无期徒刑或者死刑。\n" +
//                "　　过失犯前款罪的，处三年以上七年以下有期徒刑；情节较轻的，处三年以下有期徒刑或者拘役。\n" +
//                "　　第一百二十条　组织、领导和积极参加恐怖活动组织的，处三年以上十年以下有期徒刑；其他参加的，处三年以下有期徒刑、拘役或者管制。\n" +
//                "　　犯前款罪并实施杀人、爆炸、绑架等犯罪的，依照数罪并罚的规定处罚。\n" +
//                "　　第一百二十一条　以暴力、胁迫或者其他方法劫持航空器的，处十年以上有期徒刑或者无期徒刑；致人重伤、死亡或者使航空器遭受严重破坏的，处死刑。\n" +
//                "　　第一百二十二条　以暴力、胁迫或者其他方法劫持船只、汽车的，处五年以上十年以下有期徒刑；造成严重后果的，处十年以上有期徒刑或者无期徒刑。\n" +
//                "　　第一百二十三条　对飞行中的航空器上的人员使用暴力，危及飞行安全，尚未造成严重后果的，处五年以下有期徒刑或者拘役；造成严重后果的，处五年以上有期徒刑。\n" +
//                "　　第一百二十四条　破坏广播电视设施、公用电信设施，危害公共安全的，处三年以上七年以下有期徒刑；造成严重后果的，处七年以上有期徒刑。\n" +
//                "　　过失犯前款罪的，处三年以上七年以下有期徒刑；情节较轻的，处三年以下有期徒刑或者拘役。\n" +
//                "　　第一百二十五条　非法制造、买卖、运输、邮寄、储存\n" +
//                "枪支、弹药、爆炸物的，处三年以上十年以下有期徒刑；情节严重的，处十年以上有期徒刑、无期徒刑或者死刑。\n" +
//                "　　非法买卖、运输核材料的，依照前款的规定处罚。\n" +
//                "　　单位犯前两款罪的，对单位判处罚金，并对其直接负责的主管人员和其他直接责任人员，依照第一款的规定处罚。\n" +
//                "　　第一百二十六条　依法被指定、确定的枪支制造企业、销售企业，违反枪支管理规定，有下列行为之一的，对单位判处罚金，并对其直接负责的主管人员和其他直接责任人员，处五年以下有期徒刑；情节严重的，处五年以上十年以下有期徒刑；\n" +
//                "情节特别严重的，处十年以上有期徒刑或者无期徒刑：\n" +
//                "　　（一）以非法销售为目的，超过限额或者不按照规定的品种制造、配售枪支的；\n" +
//                "　　（二）以非法销售为目的，制造无号、重号、假号的枪支的；\n" +
//                "　　（三）非法销售枪支或者在境内销售为出口制造的枪支的。\n" +
//                "　　第一百二十七条　盗窃、抢夺枪支、弹药、爆炸物的，处三年以上十年以下有期徒刑；情节严重的，处十年以上有期徒刑、无期徒刑或者死刑。\n" +
//                "　　抢劫枪支、弹药、爆炸物或者盗窃、抢夺国家机关、军警人员、民兵的枪支、弹药、爆炸物的，处十年以上有期徒刑、无期徒刑或者死刑。\n" +
//                "　　第一百二十八条　违反枪支管理规定，非法持有、私藏枪支、弹药的，处三年以下有期徒刑、拘役或者管制；情节严重的，处三年以上七年以下有期徒刑。\n" +
//                "　　依法配备公务用枪的人员，非法出租、出借枪支的，依照前款的规定处罚。\n" +
//                "　　依法配置枪支的人员，非法出租、出借枪支，造成严重后果的，依照第一款的规定处罚。\n" +
//                "　　单位犯第二款、第三款罪的，对单位判处罚金，并对其直接负责的主管人员和其他直接责任人员，依照第一款的规定处罚。\n" +
//                "　　第一百二十九条　依法配备公务用枪的人员，丢失枪支不及时报告，造成严重后果的，处三年以下有期徒刑或者拘役。\n" +
//                "　　第一百三十条　非法携带枪支、弹药、管制刀具或者爆炸性、易燃性、放射性、毒害性、腐蚀性物品，进入公共场所或者公共交通工具，危及公共安全，情节严重的，处三年以下有期徒刑、拘役或者管制。\n" +
//                "　　第一百三十一条　航空人员违反规章制度，致使发生重大飞行事故，造成严重后果的，处三年以下有期徒刑或者拘役；造成飞机坠毁或者人员死亡的，处三年以上七年以下有期徒刑。\n" +
//                "　　第一百三十二条　铁路职工违反规章制度，致使发生铁路运营安全事故，造成严重后果的，处三年以下有期徒刑或者拘役；造成特别严重后果的，处三年以上七年以下有期徒刑。\n" +
//                "　　第一百三十三条　违反交通运输管理法规，因而发生重大事故，致人重伤、死亡或者使公私财产遭受重大损失的，处三年以下有期徒刑或者拘役；交通运输肇事后逃逸或者有其他特别恶劣情节的，处三年以上七年以下有期徒刑；因逃逸致人死亡的，处七年以上有期徒刑。\n" +
//                "　　第一百三十四条　工厂、矿山、林场、建筑企业或者其他企业、事业单位的职工，由于不服管理、违反规章制度，或者强令工人违章冒险作业，因而发生重大伤亡事故或者造成其他严重后果的，处三年以下有期徒刑或者拘役；情节特别恶劣的，处三年以上七年以下有期徒刑。\n" +
//                "　　第一百三十五条　工厂、矿山、林场、建筑企业或者其他企业、事业单位的劳动安全设施不符合国家规定，经有关部门或者单位职工提出后，对事故隐患仍不采取措施，因而发生重大伤亡事故或者造成其他严重后果的，对直接责任人员，处三年以下有期徒刑或者拘役；情节特别恶劣的，处三年以上七年以下有期徒刑。\n" +
//                "　　第一百三十六条　违反爆炸性、易燃性、放射性、毒害性、腐蚀性物品的管理规定，在生产、储存、运输、使用中发生重大事故，造成严重后果的，处三年以下有期徒刑或者拘役；后果特别严重的，处三年以上七年以下有期徒刑。\n" +
//                "　　第一百三十七条　建设单位、设计单位、施工单位、工程监理单位违反国家规定，降低工程质量标准，造成重大安全事故的，对直接责任人员，处五年以下有期徒刑或者拘役，并处罚金；后果特别严重的，处五年以上十年以下有期徒刑，并处罚金。\n" +
//                "　　第一百三十八条　明知校舍或者教育教学设施有危险，而不采取措施或者不及时报告，致使发生重大伤亡事故的，对直接责任人员，处三年以下有期徒刑或者拘役；后果特别严重的，处三年以上七年以下有期徒刑。\n" +
//                "　　第一百三十九条　违反消防管理法规，经消防监督机构通知采取改正措施而拒绝执行，造成严重后果的，对直接责任人员，\n" +
//                "处三年以下有期徒刑或者拘役；后果特别严重的，处三年以上七年以下有期徒刑。\n" +
//                "　　犯本章之罪的，可以并处没收财产。\n");
//        Embedding embedding5 = embeddingModel.embed(segment2).content();
//        embeddingStore.add(embedding5, segment5);


        Embedding queryEmbedding = embeddingModel.embed("某天，我跟我同事一起喝酒。然后我同事酒后驾车离开，结果撞到一个人。在我的怂恿之下，我同事驾车离开了现场").content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 3);

        System.out.println(relevant.toString());
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);

        System.out.println(embeddingMatch.score()); // 0.8144287765026093
        System.out.println(embeddingMatch.embedded().text()); // I like football.

    }

    @Test
    public void testRedisConnection() {
        // 连接到 Redis 服务器（默认地址和端口）
        try (Jedis jedis = new Jedis("localhost", 6380)) {
            System.out.println("连接成功");

            // 设置一个键值对
            jedis.set("mykey", "Hello, Redis!");

            // 获取并打印键值对
            String value = jedis.get("mykey");
            System.out.println("从 Redis 中读取值: " + value);

            // 其他 Redis 操作
            jedis.lpush("mylist", "item1", "item2", "item3"); // 左侧推入列表
            System.out.println("列表内容: " + jedis.lrange("mylist", 0, -1)); // 获取列表内容

            jedis.hset("myhash", "field1", "value1");
            System.out.println("哈希内容: " + jedis.hgetAll("myhash")); // 获取哈希内容
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Define Neo4j URI, Username, and Password
    private static final String NEO4J_URI = "bolt://localhost:7687";
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "test123456";

    @Test
    public void testNeo4jDatabase() {
        // Create a Neo4j Driver instance
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(USERNAME, PASSWORD))) {
            // Open a session
            try (Session session = driver.session()) {
                // Create a node
                String createResult = Neo4jUtil.createNode(session, "Alice", 30);
                System.out.println("Create Node Result: " + createResult);

                // Query the node
                String queryResult = Neo4jUtil.queryNode(session, "Alice");
                System.out.println("Query Node Result: " + queryResult);
            }
        }
    }
}
