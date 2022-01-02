package com.he.srs.bean.vo;

import com.he.srs.bean.entity.ConfigItem;
import lombok.Data;

import java.util.List;

@Data
public class ConfigItemVo extends ConfigItem {

    //子项
    private List<ConfigItem> children;

    //文本展示方式
    private String text;
}
