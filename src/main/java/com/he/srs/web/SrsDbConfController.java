package com.he.srs.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.he.srs.bean.entity.ConfigItem;
import com.he.srs.bean.query.IngestQuery;
import com.he.srs.bean.vo.ConfigItemVo;
import com.he.srs.bean.vo.IngestVo;
import com.he.srs.bean.vo.ResponseVo;
import com.he.srs.bean.vo.RootConfigVo;
import com.he.srs.service.SrsDbConfService;
import com.he.srs.util.BuildResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "dbconfig")
@RestController
@RequestMapping("/srs/dbconfig/")
public class SrsDbConfController {

    @Autowired
    private SrsDbConfService srsDbConfService;

    @ApiOperation("list")
    @GetMapping("/ingest/list")
    public ResponseVo<IPage<IngestVo>> ingest(IngestQuery ingestQuery){
        return BuildResponseUtils.buildResponse(srsDbConfService.ingest(ingestQuery));
    }

    @ApiOperation("add")
    @PostMapping("/configItem/add")
    public ResponseVo add(ConfigItem configItem){
        srsDbConfService.save(configItem);
        return BuildResponseUtils.success();
    }

    @ApiOperation("detail")
    @GetMapping("/configItem/detail")
    public ResponseVo<ConfigItemVo> detail(String id){
       return BuildResponseUtils.buildResponse(srsDbConfService.detail(id));
    }

    @ApiOperation("getRoot")
    @GetMapping("/configItem/getRoot")
    public ResponseVo<RootConfigVo> getRoot(){
        return BuildResponseUtils.buildResponse(srsDbConfService.getRoot());
    }

    @PostMapping("/loadConf")
    public void loadConf(MultipartFile file){
        srsDbConfService.loadConf(file);
    }

}
