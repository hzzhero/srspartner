package com.he.srs.bean.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class query {

    private int current = 1;

    private int size = 10;

    public <T> Page<T> genPage(){
        return new Page<>(current,size);
    }
}
