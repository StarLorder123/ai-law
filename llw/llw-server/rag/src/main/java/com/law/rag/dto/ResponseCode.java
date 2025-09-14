package com.law.rag.dto;

import lombok.Data;

public enum ResponseCode {

    SUCCESS(200,"业务成功"),
    FAILURE(500,"业务失败");

    private int code;
    private String detail;

    ResponseCode(int code,String detail){
        this.code=code;
        this.detail=detail;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
