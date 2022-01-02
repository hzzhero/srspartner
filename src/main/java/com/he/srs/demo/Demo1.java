package com.he.srs.demo;

import java.io.IOException;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

/**
 * rtsp视频源转流推送到另一个rtmp流媒体服务(转码)
 * @author hzz
 *
 */
public class Demo1 {

    /**
     * 转码方式转流
     * @param input
     * @param output
     */
    public static void recodecTransferStream(String input, String output) throws IOException{
        FFmpegFrameGrabber grabber=new FFmpegFrameGrabber(input);
        grabber.start();
        System.err.println("启动grabber");

        FFmpegFrameRecorder recorder=new FFmpegFrameRecorder(output,grabber.getImageWidth(),grabber.getImageHeight(), grabber.getAudioChannels());

        recorder.setFormat("flv");//推rtmp这里是必须设置成flv，这里是推rtp，所以必须是rtp_mpegts,转推rtsp这里写rtsp
        recorder.setFrameRate(25);
        recorder.start();
        System.err.println("启动recorder");

        Frame frame = null;
        while ((frame=grabber.grab())!=null){
            recorder.record(frame);
        }
        recorder.close();
        grabber.close();
    }

    public static void main(String[] args) throws IOException {
        recodecTransferStream("rtmp://10.32.122.174/live/livestream","rtmp://10.101.236.143/live/demo1");
    }

}

