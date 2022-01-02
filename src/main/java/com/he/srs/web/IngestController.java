package com.he.srs.web;

import com.he.srs.util.SrsConfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ingest")
public class IngestController {

    @Autowired
    private SrsConfUtil srsConfUtil;

    @GetMapping("/list")
    public List<String> list(){
        return srsConfUtil.ingestList();
    }

    @PostMapping("/add")
    public void addIngest(String id,String url){
        srsConfUtil.addIngest(id,url);
        srsConfUtil.reload();
    }

    @PostMapping("/remove")
    public void removeIngest(String[] ids){
        srsConfUtil.removeIngest(ids);
        srsConfUtil.reload();
    }
}
