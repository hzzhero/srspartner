package com.he.srs.bean.vo;

import lombok.Data;

@Data
public class ResponseVo<T> {

    private T data;

    private String msg;

    private Integer code;


}
