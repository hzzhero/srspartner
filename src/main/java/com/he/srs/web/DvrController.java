package com.he.srs.web;

import com.he.srs.util.SrsConfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dvr")
public class DvrController {
    @Autowired
    private SrsConfUtil srsConfUtil;

    @GetMapping("/info")
    public SrsConfUtil.DvrInfo info(){
        return srsConfUtil.getDvrInfo();
    }

    @PostMapping("/add")
    public void addDvr(String id){
        srsConfUtil.addDvr(id);
        srsConfUtil.reOnDvr();
    }

    @PostMapping("/remove")
    public void removeDvr(String id){
        srsConfUtil.removeDvr(id);
        srsConfUtil.reOnDvr();
    }

    @PostMapping("/turn")
    public void turnDvr(String onoff){
        srsConfUtil.turnDvr(onoff);
    }
}
