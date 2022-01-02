package com.he.srs.web;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/callback")
@Api(tags = "回调")
public class CallbackController {

    @PostMapping({"/publish","/connect","play"})
    public void publish(@RequestBody String body){
        log.info("=========================");
        log.info(body);
        log.info("=========================");
    }
}
