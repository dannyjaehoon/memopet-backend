package com.memopet.memopet.global.common.dto;

import lombok.Data;

@Data
public class RestResult {

    private Object data;
    public RestResult(Object data) {
        this.data = data;
    }

}