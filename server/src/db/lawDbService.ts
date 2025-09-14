import Database from 'better-sqlite3';
import path from 'path';
import fs from 'fs';

const DEFAULT_DB_DIR = "db";

// 基础数据库管理类
export class LawDatabaseManager {
    private static instance: LawDatabaseManager;
    private db: Database.Database;
    private dbPath: string;

    private constructor() {
        this.dbPath = path.join(DEFAULT_DB_DIR, `laws.db`);

        if (!fs.existsSync(DEFAULT_DB_DIR)) {
            fs.mkdirSync(DEFAULT_DB_DIR);
        }

        this.db = new Database(this.dbPath, {
            readonly: false,
            fileMustExist: false
        });
        this.initialize();
    }

    public static getInstance(): LawDatabaseManager {
        if (!LawDatabaseManager.instance) {
            LawDatabaseManager.instance = new LawDatabaseManager();
        }
        return LawDatabaseManager.instance;
    }

    private initialize() {
        // 初始化所有表
        this.createCaseTable();
        this.createLawDictTable();
    }

    private createCaseTable() {
        const stmt = this.db.prepare(`
            CREATE TABLE IF NOT EXISTS case_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ah TEXT,                 -- 案号
                spcx TEXT,              -- 审判程序
                ajlx TEXT,              -- 案件类型
                sycx TEXT,              -- 适用程序
                wslx TEXT,              -- 文书类型
                jafs TEXT,              -- 结案方式
                fy_name TEXT,           -- 法院名称
                fy_level TEXT,          -- 法院层级
                fy_province TEXT,       -- 法院所在省份
                fy_city TEXT,           -- 法院所在城市
                larq TEXT,              -- 立案日期
                cprq TEXT,              -- 裁判日期
                content TEXT,           -- 文本内容
                htmlcontent TEXT        -- HTML内容
            )
        `);
        stmt.run();
    }

    private createLawDictTable() {
        const stmt = this.db.prepare(`
            CREATE TABLE IF NOT EXISTS law_dict (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ftmc TEXT,              -- 法条名称
                fgzh TEXT,              -- 法规字号
                fbjg TEXT,              -- 发布机构
                fbrq TEXT,              -- 发布日期
                tgrq TEXT,              -- 通过日期
                sxrq TEXT,              -- 生效日期
                preface TEXT,           -- 前言
                president_order_name TEXT, -- 主席令名称
                president_order_no TEXT,   -- 主席令编号
                president_order_content TEXT, -- 主席令内容
                president_signatory TEXT,    -- 主席签署人
                president_sign_date TEXT,    -- 主席签署日期
                has_edition INTEGER,         -- 是否有版本
                content TEXT                 -- 内容
            )
        `);
        stmt.run();
    }

    public getDatabase(): Database.Database {
        return this.db;
    }

    public close() {
        this.db.close();
    }
}

// 案例表操作类
export class CaseRecordService {
    private db: Database.Database;

    constructor() {
        this.db = LawDatabaseManager.getInstance().getDatabase();
    }

    // 插入一条案例记录
    public insertCase(caseData: {
        ah: string,
        spcx: string,
        ajlx: string,
        sycx: string,
        wslx: string,
        jafs: string,
        fy_name: string,
        fy_level: string,
        fy_province: string,
        fy_city: string,
        larq: string,
        cprq: string,
        content: string,
        htmlcontent: string
    }) {
        const stmt = this.db.prepare(`
            INSERT INTO case_records (
                ah, spcx, ajlx, sycx, wslx, jafs, 
                fy_name, fy_level, fy_province, fy_city,
                larq, cprq, content, htmlcontent
            ) VALUES (
                ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?,
                ?, ?, ?, ?
            )
        `);
        return stmt.run(
            caseData.ah, caseData.spcx, caseData.ajlx, caseData.sycx,
            caseData.wslx, caseData.jafs, caseData.fy_name, caseData.fy_level,
            caseData.fy_province, caseData.fy_city, caseData.larq,
            caseData.cprq, caseData.content, caseData.htmlcontent
        );
    }

    // 根据ID查询案例
    public getCaseById(id: number) {
        const stmt = this.db.prepare('SELECT * FROM case_records WHERE id = ?');
        return stmt.get(id);
    }

    // 根据案号查询案例
    public getCaseByAh(ah: string) {
        const stmt = this.db.prepare('SELECT * FROM case_records WHERE ah = ?');
        return stmt.get(ah);
    }

    // 按条件查询案例
    public searchCases(conditions: {
        fy_province?: string,
        fy_city?: string,
        ajlx?: string,
        wslx?: string,
        startDate?: string,
        endDate?: string
    }) {
        let query = 'SELECT * FROM case_records WHERE 1=1';
        const params = [];

        if (conditions.fy_province) {
            query += ' AND fy_province = ?';
            params.push(conditions.fy_province);
        }
        if (conditions.fy_city) {
            query += ' AND fy_city = ?';
            params.push(conditions.fy_city);
        }
        if (conditions.ajlx) {
            query += ' AND ajlx = ?';
            params.push(conditions.ajlx);
        }
        if (conditions.wslx) {
            query += ' AND wslx = ?';
            params.push(conditions.wslx);
        }
        if (conditions.startDate) {
            query += ' AND cprq >= ?';
            params.push(conditions.startDate);
        }
        if (conditions.endDate) {
            query += ' AND cprq <= ?';
            params.push(conditions.endDate);
        }

        const stmt = this.db.prepare(query);
        return stmt.all(...params);
    }
}

// 法律条文表操作类
export class LawDictService {
    private db: Database.Database;

    constructor() {
        this.db = LawDatabaseManager.getInstance().getDatabase();
    }

    // 插入一条法律条文记录
    public insertLawDict(lawData: {
        ftmc: string,
        fgzh: string,
        fbjg: string,
        fbrq: string,
        tgrq: string,
        sxrq: string,
        preface: string,
        president_order_name: string,
        president_order_no: string,
        president_order_content: string,
        president_signatory: string,
        president_sign_date: string,
        has_edition: number,
        content: string
    }) {
        const stmt = this.db.prepare(`
            INSERT INTO law_dict (
                ftmc, fgzh, fbjg, fbrq, tgrq, sxrq, preface,
                president_order_name, president_order_no, president_order_content,
                president_signatory, president_sign_date, has_edition, content
            ) VALUES (
                ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?,
                ?, ?, ?, ?
            )
        `);
        return stmt.run(
            lawData.ftmc, lawData.fgzh, lawData.fbjg, lawData.fbrq,
            lawData.tgrq, lawData.sxrq, lawData.preface,
            lawData.president_order_name, lawData.president_order_no, lawData.president_order_content,
            lawData.president_signatory, lawData.president_sign_date, lawData.has_edition, lawData.content
        );
    }

    // 根据ID查询法律条文
    public getLawDictById(id: number) {
        const stmt = this.db.prepare('SELECT * FROM law_dict WHERE id = ?');
        return stmt.get(id);
    }

    // 根据法条名称查询法律条文
    public getLawDictByName(ftmc: string) {
        const stmt = this.db.prepare('SELECT * FROM law_dict WHERE ftmc = ?');
        return stmt.get(ftmc);
    }

    // 按条件查询法律条文
    public searchLawDict(conditions: {
        ftmc?: string,
        fbjg?: string,
        fbrq_start?: string,
        fbrq_end?: string,
        keyword?: string
    }) {
        let query = 'SELECT * FROM law_dict WHERE 1=1';
        const params = [];

        if (conditions.ftmc) {
            query += ' AND ftmc LIKE ?';
            params.push(`%${conditions.ftmc}%`);
        }
        if (conditions.fbjg) {
            query += ' AND fbjg = ?';
            params.push(conditions.fbjg);
        }
        if (conditions.fbrq_start) {
            query += ' AND fbrq >= ?';
            params.push(conditions.fbrq_start);
        }
        if (conditions.fbrq_end) {
            query += ' AND fbrq <= ?';
            params.push(conditions.fbrq_end);
        }
        if (conditions.keyword) {
            query += ' AND (content LIKE ? OR ftmc LIKE ?)';
            params.push(`%${conditions.keyword}%`);
            params.push(`%${conditions.keyword}%`);
        }

        const stmt = this.db.prepare(query);
        return stmt.all(...params);
    }
}