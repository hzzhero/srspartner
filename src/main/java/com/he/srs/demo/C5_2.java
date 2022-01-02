package com.he.srs.demo;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author hezhizhen
 * @Description TODO
 * @CreateTime 2021/11/01 15:56
 */
public class C5_2 {

    public static void captureScreen(String output,int frameRate) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop");

        //FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("title=窗口标题名称");//获取windows某个窗口画面，不支持中文，必须是窗口标题名称，而不是程序进程名称
        grabber.setFormat("gdigrab");// 基于gdigrab的输入格式
        // grabber.setFrameRate(30);//经过验证，这个帧率设置是无效的，请使用下面的帧率设置方法

        grabber.setOption("framerate", "60");// 证确设置帧率方法，直接设置60帧每秒的高帧率
//        grabber.setOption("offset_x", "100");// 截屏起始点X，全屏录制不设置此参数

//        grabber.setOption("offset_y", "100");// 截屏起始点Y，全屏录制不设置此参数

        grabber.setOption("draw_mouse", "0");//隐藏鼠标
//         grabber.setOption("draw_mouse", "1");//绘制鼠标


        grabber.start();

        Frame grab = grabber.grab();
        int width = grab.imageWidth;
        int height = grab.imageHeight;

        grabber.setImageWidth(width);// 截取的画面宽度，不设置此参数默认为全屏

        grabber.setImageHeight(height);// 截取的画面高度，不设置此参数默认为全屏

        FrameRecorder recorder = FrameRecorder.createDefault(output,width,height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264，编码
        recorder.setFormat("flv");//封装格式，如果是推送到rtmp就必须是flv封装格式
        recorder.setFrameRate(frameRate);
        recorder.start();//开启录制器

        Frame frame = null;

        long timeDump = System.currentTimeMillis();// 记录一下时间，用来简单计算一下平均帧率
        long lastTimeDump;
        long startTime = 0;
        long videoTS = 0;

        // 抓取屏幕画面
        for (int i = 0; (frame = grabber.grab()) != null; i++) {
            // 显示画面

            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS =  1000 * (System.currentTimeMillis() - startTime) ;
            recorder.setTimestamp(videoTS);
            recorder.record(frame);

            lastTimeDump = System.currentTimeMillis();
            Thread.sleep(20);
        }
        grabber.stop();
        recorder.close();
//        canvas.dispose();
    }

    public static void main(String[] args) throws Exception {
        String dest = "rtmp://10.101.236.143/live/desk";
        captureScreen(dest,25);
    }
}
