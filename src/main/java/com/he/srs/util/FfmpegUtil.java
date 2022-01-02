package com.he.srs.util;

import org.bytedeco.javacpp.Loader;
import org.springframework.stereotype.Component;

@Component
public class FfmpegUtil {

    public void process(String fromRtmp,String toRtmp) throws Exception {
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg,"-re",
                "-i", fromRtmp ,"-f" ,"flv" ,"-vcodec" ,"copy" ,"-acodec","copy", toRtmp);
        pb.inheritIO().start().waitFor();
    }
}
