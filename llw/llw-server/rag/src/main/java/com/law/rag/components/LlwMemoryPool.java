package com.law.rag.components;

import java.util.HashMap;
import java.util.Map;

public class LlwMemoryPool {

    private Map<String, LlwChatMemory> map = new HashMap<>();

    public void put(String uuid, LlwChatMemory memory) {
        map.put(uuid, memory);
    }

    public LlwChatMemory get(String uuid) {
        return map.get(uuid);
    }
}
