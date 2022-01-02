package com.he.srs.util;

/**
 * @author hezhizhen
 * @Description TODO
 * @CreateTime 2021/12/09 14:35
 */

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.ffmpeg.global.avutil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * 转流
 * @author eguid
 */
@Slf4j
public class TransStream {

    /**
     * 转流器
     * @param inputFile
     * @param outputFile
     * @throws Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     * @throws InterruptedException
     */
    public static void recordPush(String inputFile,String outputFile,int v_rs) throws Exception{

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile,0);
        recorder.setFormat("flv");
        recorder.start(grabber.getFormatContext());
        for(;;){
            recorder.recordPacket(grabber.grabPacket());
        }
    }


    /**
     * 制作ico图标
     * @param grabber 支持各种视频源和图片地址
     * @param width 宽度，不超过1280
     * @param height 高度，不超过720
     */
    public static  byte[]  createIco(FFmpegFrameGrabber grabber,int width,int height) {
        Long start = System.currentTimeMillis();
        byte[] fileByte = null;
        try (OutputStream out = new MyByteArrayOutputStream();
             FFmpegFrameRecorder recorder=new FFmpegFrameRecorder(out,300,200,0)){
            //文件数据写到内存
            recorder.setFormat("png");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_PNG);
            //ico支持bmp和png两种图片编码方式，该示例使用png
            recorder.setPixelFormat(avutil.AV_PIX_FMT_RGBA);//设置像素格式
            //像素宽高都不能超过256，width<=256&&height<=256

            recorder.setImageWidth(width>1280?1280:width);
            recorder.setImageHeight(height>720?720:height);
            recorder.start();

            Frame frame = null;
            // 只采集画面
            if((frame = grabber.grabImage())!=null) {
                // 显示画面
                recorder.record(frame);
            }
            fileByte = ((ByteArrayOutputStream) out).toByteArray();
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
        Long end = System.currentTimeMillis();
        System.out.println("花费时间：" + (end - start) + "");
        return fileByte;
    }

    /**
     * 制作ico图标
     * @param url 支持各种视频源和图片地址
     * @param width 宽度，不超过1280
     * @param height 高度，不超过720
     */
    public static  byte[]  createIco(String url,int width,int height) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(url);
        grabber.start();

        OutputStream out = new MyByteArrayOutputStream();
        byte[] fileByte = null;

        //文件数据写到内存
        FFmpegFrameRecorder recorder=new FFmpegFrameRecorder(out,300,200,0);
        recorder.setFormat("png");
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_PNG);
        //ico支持bmp和png两种图片编码方式，该示例使用png
        recorder.setPixelFormat(avutil.AV_PIX_FMT_RGBA);//设置像素格式
        //像素宽高都不能超过256，width<=256&&height<=256

        recorder.setImageWidth(width>1280?1280:width);
        recorder.setImageHeight(height>720?720:height);
        recorder.start();

        Frame frame = null;
        // 只采集画面
        if((frame = grabber.grabImage())!=null) {
            // 显示画面
            recorder.record(frame);
        }

        fileByte = ((ByteArrayOutputStream) out).toByteArray();

        recorder.close();
        grabber.close();

        return fileByte;
    }

    /**
     * 制作ico图标
     * @param url 支持各种视频源和图片地址
     * @param file  图片写入流
     * @param width 宽度，不超过1280
     * @param height 高度，不超过720
     */
    public static  void createIco(String url,String file,int width,int height) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(url);
        grabber.start();

        Frame frame = null;
        // 只采集画面
        int i = 0;
        FFmpegFrameRecorder recorder = null;
        while ((frame = grabber.grabImage())!=null) {
            Long start = System.currentTimeMillis();
            i++;
            //文件数据写到内存
            recorder=new FFmpegFrameRecorder(i + file,0);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_PNG);
            //ico支持bmp和png两种图片编码方式，该示例使用png
            recorder.setPixelFormat(avutil.AV_PIX_FMT_RGBA);//设置像素格式
            //像素宽高都不能超过256，width<=256&&height<=256

            recorder.setImageWidth(width>1280?1280:width);
            recorder.setImageHeight(height>720?720:height);
            recorder.start();
            // 显示画面
            recorder.record(frame);
            recorder.close();
            Long end = System.currentTimeMillis();

            System.out.println("花费时间：" + (end - start) + "");
//            Thread.sleep(50);
            if(i > 10){
                break;
            }
        }
        grabber.close();
    }

    public static void main(String[] args) {
        try {
            createIco("rtmp://10.32.122.174/live/livestream","aaa.png",320,200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}