package com.he.srs.bean.vo;

import lombok.Data;

/**
 * @author hezhizhen
 * @Description TODO
 * @CreateTime 2021/12/09 16:07
 */
@Data
public class PullStreamRequest {

    private String id;

    //流地址
    private String streamAdrr;

    //帧率
    private int frameRate = 25;

}
