package com.he.srs.bean.vo;

import com.he.srs.bean.entity.ConfigItem;
import lombok.Data;

import java.util.List;

@Data
public class IngestVo extends ConfigItem {

    //子项
    private List<ConfigItem> children;

    //文本展示方式
    private String text;

    //以下关键配置
    //采集类型  stream  file
    private String inputType;
    //采集地址
    private String inputUrl;

    //engine.enabled
    private String engineEnabled;
    //engine.vcodec
    private String engineVcodec;
    //engine.acodec
    private String engineAcodec;
    //engine.output
    private String engineOutput;
    //engine.perfile.rtsp_tansport
    private String perfileTcp;

    //当前是否在录像
    private boolean dvrOn;
    //是否有历史录像
    private boolean hasRecord;
}
