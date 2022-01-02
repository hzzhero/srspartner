package com.he.srs.util;

import cn.hutool.core.util.ObjectUtil;
import com.he.srs.bean.entity.ConfigItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public abstract class Parser {

    public static final String PARAM_TEMPLATE = "%s %s ;";

    public static final String BLOCK_TEMPLATE_PRE = "%s %s {\n  ";
    public static final String BLOCK_TEMPLATE_SUF = "\n}";

    /**********************正向转换（ 数据库记录--> 文本配置）************************/
    public List<ConfigItem> getList(List<ConfigItem> all, String id) {
        List<ConfigItem> collect = all.stream().filter(ci -> {
            if (ObjectUtil.isEmpty(id)) {
                return ObjectUtil.isEmpty(ci.getPid());
            } else {
                return id.equals(ci.getPid());
            }
        }).collect(Collectors.toList());
        return collect;
    }

    public abstract String getText(List<ConfigItem> all, String id);

    protected String genParamTxt(ConfigItem ci) {
        return String.format(PARAM_TEMPLATE, ci.getName(), ci.getVal());
    }

    protected String genBlockTxt(ConfigItem ci) {
        String val = ci.getVal();
        if (ObjectUtil.isEmpty(val)) {
            val = "";
        }
        return String.format(BLOCK_TEMPLATE_PRE, ci.getName(), val);
    }

    /********************反向转换（ 文本配置--> 数据库记录）*********************/
    public void genConfigItems(List<Line> lines,String pid,List<ConfigItem> all) {
        Stack<Line> stack = new Stack<>();
        for (Line lineObj : lines) {
            String line = lineObj.getLine().trim();
            Integer i = lineObj.getIndex();
            List<Line> ll = new ArrayList<>();
            Integer ii = 0;
            if(isBlockSuf(line)){
                while(true){
                    Line pop = stack.pop();
                    if(isBlock(pop.getLine())){
                        ii  = pop.getIndex();
                       break;
                    }else {
                        ll.add(pop);
                    }
                }
                genConfigItems(ll,String.valueOf(ii),all);
            }else {
                if(!stack.isEmpty()){
                    stack.push(lineObj);
                    continue;
                }
            }
            String[] split = line.substring(0,line.length() - 1).split("[ ]+");
            if (isParam(line)) {
                ConfigItem c = new ConfigItem();
                c.setId(String.valueOf(i));
                c.setName(split[0]);
                c.setVal(split[1]);
                c.setPid(pid);
                c.setType(1);
                c.setSort(i);
                all.add(c);
            } else if(isBlock(line)){
                //split的长度是1还是2？
                String val = "";
                if (split.length == 2) {
                    val = split[1];
                }
                ConfigItem c = new ConfigItem();
                String childPid = String.valueOf(i);
                c.setId(childPid);
                c.setPid(pid);
                c.setType(2);
                c.setSort(i);
                c.setName(split[0]);
                c.setVal(val);
                all.add(c);

                stack.push(lineObj);
            }
        }
    }

    private boolean isParam(String line) {
        return line.trim().endsWith(";");
    }

    private boolean isBlock(String line) {
        return line.trim().endsWith("{");
    }

    private boolean isBlockSuf(String line) {
        return line.trim().equals("}");
    }

    @Data
    @AllArgsConstructor
    public static class Line {
        String line;
        Integer index;
    }

}
