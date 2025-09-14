package com.law.rag.components;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallzh.BgeSmallZhEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.data.embedding.Embedding;

import java.util.List;

public class LlwEmbeddingStore {

    private EmbeddingModel embeddingModel;

    private EmbeddingStore<TextSegment> embeddingStore;

    private final int MAX_RESULTS = 5;

    public LlwEmbeddingStore(String host, Integer port, String collection) {
        embeddingModel = new BgeSmallZhEmbeddingModel();

        
        embeddingStore = MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName(collection)
                .dimension(embeddingModel.dimension())
                .build();
    }

    public String addText(String content) {
        TextSegment segment = TextSegment.from(content);
        Embedding embedding = embeddingModel.embed(segment).content();
        return embeddingStore.add(embedding, segment);
    }

    public void removeText(String id){
        embeddingStore.remove(id);
    }

    public List<EmbeddingMatch<TextSegment>> queryEmbedding(String content) {
        return queryEmbedding(content, MAX_RESULTS);
    }

    public List<EmbeddingMatch<TextSegment>> queryEmbedding(String content, int max_result) {
        Embedding queryEmbedding = embeddingModel.embed(content).content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, max_result);
        return relevant;
    }
}
