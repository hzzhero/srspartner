package com.he.srs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.he.srs.bean.entity.ConfigItem;
import com.he.srs.bean.query.IngestQuery;
import com.he.srs.bean.vo.ConfigItemVo;
import com.he.srs.bean.vo.IngestVo;
import com.he.srs.bean.vo.RootConfigVo;
import com.he.srs.dao.ConfigItemMapper;
import com.he.srs.service.SrsDbConfService;
import com.he.srs.util.ConfigItemParser;
import com.he.srs.util.Parser;
import com.he.srs.util.RootConfigParser;
import org.springframework.stereotype.Service;
import com.he.srs.util.Constants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SrsDbConfServiceImpl extends ServiceImpl<ConfigItemMapper, ConfigItem> implements SrsDbConfService {

    @Override
    public IPage<IngestVo> ingest(IngestQuery ingestQuery) {
        IPage<ConfigItem> page = ingestQuery.genPage();
        page = this.page(page,
                Wrappers.<ConfigItem>lambdaQuery()
                        .eq(ConfigItem::getName, Constants.INGEST)
                        .eq(ConfigItem::getVal, ingestQuery.getVal())
        );
        IPage<IngestVo> convert = page.convert(configItem -> {
            IngestVo ingestVo = BeanUtil.copyProperties(configItem, IngestVo.class);
            return ingestVo;
        });
        return convert;
    }

    @Override
    public ConfigItemVo detail(String id) {
        List<ConfigItem> list = this.list();
        Parser parser = new ConfigItemParser();
        String text = parser.getText(list, id);
        ConfigItem byId = this.getById(id);
        ConfigItemVo configItemVo = BeanUtil.copyProperties(byId, ConfigItemVo.class);
        configItemVo.setText(text);

        List<ConfigItem> list1 = parser.getList(list, id);
        configItemVo.setChildren(list1);

        return configItemVo;
    }

    @Override
    public RootConfigVo getRoot() {
        List<ConfigItem> list = this.list();
        Parser parser = new RootConfigParser();
        String text = parser.getText(list, null);

        RootConfigVo rootConfigVo = new RootConfigVo();
        rootConfigVo.setText(text);

        List<ConfigItem> list1 = parser.getList(list, null);
        rootConfigVo.setChildren(list1);
        return rootConfigVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void loadConf(MultipartFile file) {
        try {
            this.remove(Wrappers.lambdaQuery());
            String srsconfPath = FileUtil.getTmpDir().getAbsolutePath() + "/srs.conf";
            File f = new File(srsconfPath);
            file.transferTo(f);
            List<String> strings = FileUtil.readLines(f, StandardCharsets.UTF_8);
            strings = strings.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            List<Parser.Line> lines = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                lines.add(new Parser.Line(strings.get(i), i));
            }
            Parser parser = new ConfigItemParser();
            List<ConfigItem> cis = new ArrayList<>();
            parser.genConfigItems(lines, null, cis);
            cis.forEach(System.out::println);
            this.saveBatch(cis);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
