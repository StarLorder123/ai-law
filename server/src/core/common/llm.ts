import { ChatOllama, ChatOllamaInput } from "@langchain/ollama";

const DEFAULT_BASEURL = "http://localhost:11434";
const DEFAULT_MODEL = "qwen2.5:7b";
/**tempareture 在0.0-1.0之间浮动。越靠近0，表示越精准；反之则会趋向于更灵活 */
const DEFAULT_TEMPARETURE = 0.2;

export const ollama = new ChatOllama({
    baseUrl: process.env.BASEURL ?? DEFAULT_BASEURL,
    model: process.env.MODEL ?? DEFAULT_MODEL,
    temperature: DEFAULT_TEMPARETURE
})

export function createOllamaModel() {
    return new ChatOllama({
        baseUrl: process.env.BASEURL ?? DEFAULT_BASEURL,
        model: process.env.MODEL ?? DEFAULT_MODEL,
        temperature: DEFAULT_TEMPARETURE
    });
}

export function createModel(params: ChatOllamaInput) {
    return new ChatOllama(params);
}