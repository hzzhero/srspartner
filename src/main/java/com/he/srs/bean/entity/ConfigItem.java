package com.he.srs.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("config_item")
public class ConfigItem {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String val;

    private Integer type;

    private String pid;

    private String belong = String.valueOf(1);

    private Integer sort;

}
