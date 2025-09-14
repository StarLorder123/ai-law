import { Router } from "express";
import { chatStream, getSessionList, getSessionChat } from "../controller/chat.controller";

const router = Router();

router.post('/chat', chatStream);
router.post('/sessions', getSessionList);
router.post('/session/chat', getSessionChat);

export default router;