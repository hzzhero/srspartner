package com.he.srs.bean.vo;

import lombok.Data;

/**
 * @author hezhizhen
 * @Description TODO
 * @CreateTime 2021/12/10 09:59
 */
@Data
public class CallbackRequest {

    private String action;

    private Integer client_id;

    private String ip;

    private String vhost;

    private String app;

    private String tcUrl;

    private String stream;

    private String param;

    private String pageUrl;

    private String cwd;

    private String file;

    private String url;

    private String m3u8;

    private String m3u8_url;
}
