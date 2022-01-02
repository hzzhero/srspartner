package com.he.srs.util.srsInterpreter;

import java.util.HashMap;

public class Context {

    private HashMap<Integer,String> map = new HashMap();

    //key  行号   value 这一行类容的字符串
    public void assign(Integer key, String value) {

        //往环境类中设值

    }

    public String  lookup(Integer key) {

        //获取存储在环境类中的值
        return map.get(key);
    }

}
