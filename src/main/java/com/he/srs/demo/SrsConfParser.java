package com.he.srs.demo;

import com.github.odiszapc.nginxparser.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SrsConfParser {

    public static void parse() {
        try {
            NgxConfig conf = NgxConfig.read("D:/产品/视觉算仓/conf/demo.conf");
            NgxParam workers = conf.findParam("listen");       // Ex.1
            String value = workers.getValue();// "1"
            System.out.println(value);
            NgxParam enable = conf.findParam("http_api", "enabled"); // Ex.2
            System.out.println(enable.toString());

            NgxBlock rtmpServers0 = conf.findBlock("vhost");

            List<NgxEntry> rtmpServers = conf.findAll(NgxConfig.BLOCK, "vhost"); // Ex.3
            Optional<NgxEntry> any = rtmpServers.stream().filter(item -> {
                NgxBlock b = (NgxBlock) item;
                return "demo.srs.com".equals(b.getValue());
            }).findAny();
            for (NgxEntry entry : rtmpServers) {
                NgxBlock b = (NgxBlock) entry;
                String v = b.getValue();// "server"
                if("demo.srs.com".equals(v)){
                    Collection<NgxEntry> entries = b.getEntries();
                    for (NgxEntry en : entries){
                        if(en instanceof NgxParam){
                            NgxParam ngxParam = (NgxParam) en;
                            log.info("这是一个param: {}",ngxParam.getValue());
                        }else if (en instanceof NgxBlock){
                            NgxBlock block = (NgxBlock) en;
                            Collection<NgxEntry> entries1 = block.getEntries();
                            log.info("这是一个block: {}",block.getName());

                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dump() {
        NgxConfig conf = null;
        try {
            conf = NgxConfig.read("D:/产品/视觉算仓/conf/demo.conf");
            NgxDumper dumper = new NgxDumper(conf);
            dumper.dump(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        parse();
    }
}
