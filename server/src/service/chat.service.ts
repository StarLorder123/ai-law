import { LLMResult } from "@langchain/core/outputs";
import { ollama } from "../core/common/llm";
import { PromptTemplate } from "@langchain/core/prompts";
import { messagesCollection } from "../core/common/message";

export function baseChat(req: any, res: any) {
    // 从 req.body 中提取参数
    const { projectid, sessionid, message, chatObjectType, userid } = req.body;

    if (sessionid) {
        messagesCollection.getSession(userid, projectid, sessionid)?.addHumanMessage(message);
    }
    const prompt = PromptTemplate.fromTemplate('{base_content}');
    const chain = prompt.pipe(ollama);
    chain.invoke(
        {
            base_content: message
        },
        {
            callbacks: [
                {
                    handleLLMNewToken(token) {
                        const data = {
                            message: token,
                            timestamp: new Date().toISOString(),
                        };
                        res.write(JSON.stringify(data));
                        process.stdout.write(token);
                    },
                    handleLLMEnd(output: LLMResult, runId: string) {
                        const finishData = {
                            type: 'finish'
                        }
                        console.log('LLM 结束', `output:${JSON.stringify(output.generations[0][0].text)}`, runId);
                        if (sessionid) {
                            messagesCollection.getSession(userid, projectid, sessionid)?.addAIMessage(output.generations[0][0].text);
                        }
                        res.write(JSON.stringify(finishData));
                        res.end();
                    }
                }
            ]
        }
    )
}