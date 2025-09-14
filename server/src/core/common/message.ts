import { AIMessage, BaseMessage, HumanMessage, SystemMessage } from "@langchain/core/messages";
import { ChatDatabase } from "../../db/chatDbService";

class BaseSessionMessagesList {

    messages: BaseMessage[] = [];
    chatdb: ChatDatabase;

    constructor(
        private readonly userid: string,
        private readonly projectid: string,
        private readonly sessionid: string) {
        this.chatdb = new ChatDatabase(userid);
        this.getSessionFromDB(projectid, sessionid);
    }

    private addMessage(message: BaseMessage) {
        this.messages.push(message);
    }

    addHumanMessage(content: string) {
        this.chatdb.insertChatRecord(this.projectid, this.sessionid, 'human', content);
        this.addMessage(new HumanMessage(content));
    }

    addAIMessage(content: string) {
        this.chatdb.insertChatRecord(this.projectid, this.sessionid, 'ai', content);
        this.addMessage(new AIMessage(content));
    }

    addSystemMessage(content: string) {
        this.chatdb.insertChatRecord(this.projectid, this.sessionid, 'system', content);
        this.addMessage(new SystemMessage(content));
    }

    private getSessionFromDB(projectid: string, sessionid: string) {
        const arr = this.chatdb.getChatRecordsByProjectAndSession(projectid, sessionid);
        const messages: BaseMessage[] = []
        for (const i in arr) {
            switch ((arr[i] as any).chat_object_type) {
                case 'ai':
                    messages.push(new AIMessage((arr[i] as any).message));
                case 'human':
                    messages.push(new HumanMessage((arr[i] as any).message));
                case 'system':
                    messages.push(new SystemMessage((arr[i] as any).message));
            }
        }
        this.messages = messages;
    }
}

class MessagesCollection {
    /**
     * key: userid-projectid-sessionid
     */
    private sessions: Map<string, BaseSessionMessagesList> = new Map();

    /**
     * 获取session。如果不存在则创建之后再返回，如果存在就直接返回
     * @param userid 
     * @param projectid 
     * @param sessionid 
     * @returns 
     */
    getSession(userid: string, projectid: string, sessionid: string) {
        const key = `${userid}-${projectid}-${sessionid}`;
        if (!this.sessions.has(key)) {
            const session = new BaseSessionMessagesList(userid, projectid, sessionid);
            this.sessions.set(key, session);
            return session;
        } else {
            return this.sessions.get(key);
        }
    }

    private splitSessionKey(key: string): { userid: string; projectid: string; sessionid: string } {
        const [userid, projectid, sessionid] = key.split('-');
        return {
            userid,
            projectid,
            sessionid
        };
    }
}

/**
 * 全局性的消息集合
 * 用于在内存中缓存对应的message List即可
 */
export const messagesCollection = new MessagesCollection();