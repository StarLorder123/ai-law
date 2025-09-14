import { Request, Response, NextFunction } from "express";

/**
 * 全局错误处理器
 * 可以用 next(new Error("Something went wrong")) 来触发错误
 * @param err 
 * @param req 
 * @param res 
 * @param next 
 */
export const errorHandler = (err: Error, req: Request, res: Response, next: NextFunction) => {
    console.error(err.stack);
    res.status(500).json({ error: "Internal Server Error" });
};
