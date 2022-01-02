package com.he.srs.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import com.github.odiszapc.nginxparser.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author hezhizhen
 * srs 配置文件工具类
 */
@Slf4j
@Component
public class SrsConfUtil {

    @Value("${srs.conf.path:/opt/srs/conf/srs.conf}")
    private String path;

    private NgxConfig conf;

    @Value("${srs.dvr.path:/opt/srs/objs/nginx/html/live/dvr/}")
    private String flvPath;

    @Value("${srs.reload.ul:http://localhost:1985/api/v1/raw?rpc=reload}")
    private String reloadUrl;

    public void setFlvPath(String flvPath) {
        this.flvPath = flvPath;
    }

    public static final String DEFAULT_VHOST = "__defaultVhost__";//默认vhost

    public static final String DEFAULT_APP = "live";//默认app

    public static final Integer DEFAULT_DVR_SEGMENT = 30;//默认录像片段30s

    public void init() {
        File f = new File(path);
        if (!f.exists()) {
            //读取默认的配置文件
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("srs.conf");
            FileUtil.writeFromStream(resourceAsStream, f);
        }
        try {
            this.conf = NgxConfig.read(path);
        } catch (IOException e) {
            log.error("读取配置文件失败");
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据时刻查找录像
     *
     * @param point
     * @return
     */
    public Optional<String> searchFlv(Long point, String id) {
        //默认录像在此目录下/opt/srs/objs/nginx/html/
        String idFlvPath = flvPath + id;
        List<String> strings = FileUtil.listFileNames(idFlvPath);
        Optional<String> any = strings.stream().filter(item -> {
            return item.compareTo(String.valueOf(point - DEFAULT_DVR_SEGMENT)) >= 0 && item.compareTo(String.valueOf(point)) <= 0;
        }).findAny();
        return any;
    }

    /**
     * 根据时间段查找录像
     *
     * @param start
     * @param end
     * @param id
     * @return
     */
    public List<String> searchFlv(Long start, Long end, String id) {
        String idFlvPath = flvPath + id;
        List<String> strings = FileUtil.listFileNames(idFlvPath);
        List<String> collect = strings.stream().filter(item -> {
            return item.compareTo(String.valueOf(start - DEFAULT_DVR_SEGMENT)) >= 0 && item.compareTo(String.valueOf(end + DEFAULT_DVR_SEGMENT)) <= 0;
        }).collect(Collectors.toList());

        return collect;
    }

    /**
     * 对一路流取消录像
     *
     * @param id
     */
    public void removeDvr(String id) {
        this.init();
        Optional<NgxBlock> any = getDefaultHost();
        if (!any.isPresent()) {
            return;
        }
        NgxBlock host = (NgxBlock) any.get();
        NgxBlock dvr = host.findBlock("dvr");
        String streamUrl = "live/" + id;
        if (ObjectUtil.isEmpty(dvr)) {
            log.warn("暂无录像配置");
            log.warn("id:{}--这路流无需取消录像");
            return;
        } else {
            NgxParam dvr_apply = dvr.findParam("dvr_apply");
            List<String> values = dvr_apply.getValues();
            values.removeIf(item -> item.equals(streamUrl));
            dvr.remove(dvr_apply);
            dvr_apply = new NgxParam();
            if (values.size() < 1) {
                NgxParam enab = dvr.findParam("enabled");
                dvr.remove(enab);
                enab = new NgxParam();
                enab.addValue("enabled off");
                dvr.addEntry(enab);

                values.add("none");
            }
            values.add(0, "dvr_apply");
            dvr_apply.addValue(String.join(" ", values));
            dvr.addEntry(dvr_apply);
        }
        this.dump();
    }

    /**
     * 删除流
     */
    public void removeIngest(String[] ids) {
        this.init();
        for (String id : ids) {
            Optional<NgxBlock> op = this.hasIngest(id);
            if (!op.isPresent()) {//已存在
                log.warn("这路流未加入，id：{}", id);
                continue;
            }
            NgxBlock ingest = op.get();
            this.conf.remove(ingest);
        }
        this.dump();
    }

    /**
     * 加入一路采集rtsp流
     */
    public void addIngest(String id, String url) {
        this.init();
        Optional<NgxBlock> op = this.hasIngest(id);
        if (op.isPresent()) {//已存在
            NgxBlock ingest = op.get();
            NgxBlock input = ingest.find(NgxBlock.class, "input");
            NgxParam inputUrl = input.findParam("url");
            log.warn("这路流已经加入，id：{},url：{}", id, inputUrl);
            return;
        }

        Optional<NgxBlock> any = getDefaultHost();
        if (any.isPresent()) {
            NgxBlock host = any.get();
            NgxBlock block = generBlock(id, url);
            host.addEntry(block);
            this.dump();
        }

    }

    /**
     * 执行 srs reload
     */
    public void reload() {
        String body = HttpRequest.post(reloadUrl).execute().body();
        log.info("srs-reload执行结果：{}", body);
    }

    private void dump() {
        NgxDumper dumper = new NgxDumper(this.conf);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(this.path);
            dumper.dump(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * turn on or off  dvr
     */
    public void turnDvr(String onoff) {
        this.init();
        Optional<NgxBlock> any = getDefaultHost();
        if (!any.isPresent()) {
            return;
        }
        NgxBlock host = (NgxBlock) any.get();
        NgxBlock dvr = host.findBlock("dvr");
        NgxParam enabled = dvr.findParam("enabled");
        dvr.remove(enabled);
        enabled = new NgxParam();
        enabled.addValue("enabled " + onoff);
        dvr.addEntry(enabled);
        this.dump();
        this.reload();
    }

    /**
     * srs开启和取消录像有bug，用关闭在开启来解决
     */
    public void reOnDvr() {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.turnDvr("off");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.turnDvr("on");
        });
        t.start();
    }

    /**
     * 对一路流进行录像
     *
     * @param id
     */
    public void addDvr(String id) {
        this.init();
        Optional<NgxBlock> any = getDefaultHost();
        if (!any.isPresent()) {
            return;
        }
        NgxBlock host = (NgxBlock) any.get();
        NgxBlock dvr = host.findBlock("dvr");
        String streamUrl = "live/" + id;
        if (ObjectUtil.isEmpty(dvr)) {
            dvr = new NgxBlock();
            dvr.addValue("dvr");

            NgxParam enabled = new NgxParam();
            enabled.addValue("enabled on");
            dvr.addEntry(enabled);

            NgxParam dvr_path = new NgxParam();
            dvr_path.addValue("dvr_path ./objs/nginx/html/[app]/dvr/[stream]/[timestamp].flv");
            dvr.addEntry(dvr_path);

            NgxParam dvr_plan = new NgxParam();
            dvr_plan.addValue("dvr_plan segment");
            dvr.addEntry(dvr_plan);

            NgxParam dvr_duration = new NgxParam();
            dvr_duration.addValue("dvr_duration " + DEFAULT_DVR_SEGMENT);
            dvr.addEntry(dvr_duration);

            NgxParam dvr_apply = new NgxParam();
            dvr_apply.addValue("dvr_apply  " + streamUrl);
            dvr.addEntry(dvr_apply);

            host.addEntry(dvr);
        } else {
            NgxParam enab = dvr.findParam("enabled");
            dvr.remove(enab);
            enab = new NgxParam();
            enab.addValue("enabled on");
            dvr.addEntry(enab);

            NgxParam dvr_apply = dvr.findParam("dvr_apply");
            List<String> values = dvr_apply.getValues();
            values.removeIf(item -> item.equals("all") || item.equals("none"));
            Optional<String> any1 = values.stream().filter(item -> item.equals(streamUrl)).findAny();
            if (any1.isPresent()) {
                log.warn("这路流已经在录像中：id--{}", id);
                return;
            }
            dvr.remove(dvr_apply);
            dvr_apply = new NgxParam();
            values.add(0, "dvr_apply");
            values.add(streamUrl);
            dvr_apply.addValue(String.join(" ", values));
            dvr.addEntry(dvr_apply);
        }
        this.dump();
    }

    /**
     * 是否存在这路流
     *
     * @param id
     * @return
     */
    public Optional<NgxBlock> hasIngest(String id) {
        Optional<NgxBlock> any = getDefaultHost();
        if (!any.isPresent()) {
            return Optional.empty();
        }
        NgxBlock host = (NgxBlock) any.get();
        List<NgxEntry> ingests = host.findAll(NgxConfig.BLOCK, "ingest");
        if (ObjectUtil.isEmpty(ingests)) {
            return Optional.empty();
        }
        Optional<NgxBlock> any1 = ingests.stream()
                .map(a -> ((NgxBlock) a))
                .filter(item -> id.equals(item.getValue()))
                .findAny();
        return any1;
    }

    /**
     * 当前采集的流列表
     *
     * @return
     */
    public List<String> ingestList() {
        Optional<NgxBlock> any = getDefaultHost();
        if (!any.isPresent()) {
            return Collections.emptyList();
        }
        NgxBlock host = (NgxBlock) any.get();
        List<NgxEntry> ingests = host.findAll(NgxConfig.BLOCK, "ingest");
        if (ObjectUtil.isEmpty(ingests)) {
            return Collections.emptyList();
        }
        return ingests.stream().map(a -> ((NgxBlock) a))
                //.filter(item -> DEFAULT_VHOST.equals(item.getValue()))
                .map(item -> {
                    List<NgxEntry> entries = item.findAll(NgxConfig.BLOCK, "engine");
                    NgxBlock engine = (NgxBlock) entries.get(0);
                    NgxParam output = engine.findParam("output");
                    String value = output.getValue();
                    return value.substring(value.lastIndexOf("/") + 1);
                }).collect(Collectors.toList());
    }

    /**
     * 获取录像信息
     *
     * @return
     */
    public DvrInfo getDvrInfo() {
        this.init();
        Optional<NgxBlock> any = getDefaultHost();
        if (!any.isPresent()) {
            return null;
        }
        NgxBlock host = (NgxBlock) any.get();
        NgxBlock dvr = host.findBlock("dvr");
        if (ObjectUtil.isEmpty(dvr)) {
            return null;
        }
        DvrInfo dvrInfo = new DvrInfo();
        NgxParam enab = dvr.findParam("enabled");
        dvrInfo.setEnabled(enab.getValue());
        NgxParam dvr_apply = dvr.findParam("dvr_apply");
        List<String> values = dvr_apply.getValues();
        dvrInfo.setStreams(values);

        //读取dvr下的文件夹，文件夹的名字就是有录像文件的流
        if (FileUtil.isDirectory(flvPath)) {
            List<String> files = FileUtil.listFileNames(flvPath);
            dvrInfo.setRecords(files);
        }
        return dvrInfo;
    }

    private Optional<NgxBlock> getDefaultHost() {
        List<NgxEntry> vhosts = this.conf.findAll(NgxConfig.BLOCK, "vhost");
        Optional<NgxBlock> any = vhosts.stream()
                .map(a -> ((NgxBlock) a))
                .filter(item -> DEFAULT_VHOST.equals(item.getValue()))
                .findAny();
        return any;
    }

    /**
     * 生成ingest rtsp
     *
     * @param id
     * @param url
     * @return
     */
    private NgxBlock generBlock(String id, String url) {
        NgxBlock ret = new NgxBlock();
        ret.addValue("ingest");
        ret.addValue(id);

        NgxParam enabled = new NgxParam();
        enabled.addValue("enabled on");
        ret.addEntry(enabled);

        NgxBlock input = new NgxBlock();
        input.addValue("input");
        NgxParam type = new NgxParam();
        type.addValue("type stream");
        input.addEntry(type);
        NgxParam inputurl = new NgxParam();
        inputurl.addValue("url");
        inputurl.addValue(url);
        input.addEntry(inputurl);
        ret.addEntry(input);

        NgxParam ffmpeg = new NgxParam();
        ffmpeg.addValue("ffmpeg ./objs/ffmpeg/bin/ffmpeg");
        ret.addEntry(ffmpeg);

        NgxBlock engine = new NgxBlock();
        engine.addValue("engine");
        NgxParam engineEnabled = new NgxParam();
        if (url.contains("rtsp")) {
            engineEnabled.addValue("enabled on");
        } else if (url.contains("rtmp")) {
            engineEnabled.addValue("enabled off");
        } else {
            engineEnabled.addValue("enabled off");
        }
        engine.addEntry(engineEnabled);
        NgxParam vcodec = new NgxParam();
        vcodec.addValue("vcodec copy");
        engine.addEntry(vcodec);
        NgxParam acodec = new NgxParam();
        acodec.addValue("acodec copy");
        engine.addEntry(acodec);
        NgxParam output = new NgxParam();
        output.addValue("output");
        output.addValue("rtmp://127.0.0.1:[port]/live?vhost=[vhost]/" + id);
        engine.addEntry(output);
        NgxBlock perfile = new NgxBlock();
        perfile.addValue("perfile");
        NgxParam rtsp_transport = new NgxParam();
        rtsp_transport.addValue("rtsp_transport tcp");
        perfile.addEntry(rtsp_transport);
        engine.addEntry(perfile);
        ret.addEntry(engine);

        return ret;
    }

    public static void main(String[] args) {
        String path = "D:/srs2.conf";
        SrsConfUtil srsConfUtil = new SrsConfUtil();
        Optional<NgxBlock> livestream = srsConfUtil.hasIngest("livestream");
        System.out.println(livestream);

//        srsConfUtil.addIngestRtsp("hzz111","rtmp://10.32.122.174/live/livestream");
        srsConfUtil.removeDvr("livestream2");

        srsConfUtil.setFlvPath("C:/Users/Administrator/Desktop/fsdownload/");

        List<String> livestream2 = srsConfUtil.searchFlv(1640679892317L, 1640680014799L, "livestream2");
        livestream2.forEach(System.out::println);
    }

    @Data
    public static class DvrInfo {

        //是否开启
        private String enabled;

        //当前是哪些流在录制列表
        private List<String> streams;

        //当前有的录像
        private List<String> records;
    }
}

