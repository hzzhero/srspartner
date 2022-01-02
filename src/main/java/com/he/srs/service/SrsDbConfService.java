package com.he.srs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.he.srs.bean.entity.ConfigItem;
import com.he.srs.bean.query.IngestQuery;
import com.he.srs.bean.vo.ConfigItemVo;
import com.he.srs.bean.vo.IngestVo;
import com.he.srs.bean.vo.RootConfigVo;
import org.springframework.web.multipart.MultipartFile;

public interface SrsDbConfService extends IService<ConfigItem> {

    IPage<IngestVo> ingest(IngestQuery ingestQuery);

    ConfigItemVo detail(String id);

    RootConfigVo getRoot();

    /**
     * 初始化配置
     */
    void loadConf(MultipartFile file);
}
