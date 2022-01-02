package com.he.srs.bean.query;

import lombok.Data;

import javax.management.Query;

@Data
public class ConfigItemQuery extends Query {

    private String name;

    private String val;

    private String pid;

    private String type;

    private String belong;
}
