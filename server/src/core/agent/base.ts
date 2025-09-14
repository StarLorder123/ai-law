import { BaseMessage } from "@langchain/core/messages";
import { ChatOllama, Ollama } from "@langchain/ollama";
import { toolsArray } from "../tools/base";

export class BaseAgent {
    model: Ollama | ChatOllama;
    messages: BaseMessage[] = [];

    constructor(model: Ollama | ChatOllama) {
        this.model = model;
    }

    setMessages(messages: BaseMessage[]) {
        this.messages = messages;
    }

    addMessage(message: BaseMessage) {
        this.messages.push(message);
    }

    getModelWithTools() {
        return this.model.bind({
            tools: toolsArray
        })
    }

}