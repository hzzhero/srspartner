package com.he.srs.demo;

import cn.hutool.core.io.FileUtil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Demo3 {

    /**
     * 转码方式转流
     * @param input
     */
    public static void snapshot(String input) throws IOException {
        FFmpegFrameGrabber grabber=new FFmpegFrameGrabber(input);
        grabber.start();
        System.err.println("启动grabber");
        Frame frame = null;
        while ((frame=grabber.grabKeyFrame())!=null){
            Long start = System.currentTimeMillis();
            BufferedImage bufferedImage = Java2DFrameUtils.toBufferedImage(frame);
            byte[] bytes = imageToBytes(bufferedImage);
            //System.out.println(bytes.length);
            FileUtil.writeBytes(bytes, "D:/pic/"+System.currentTimeMillis()+".png");
            Long end = System.currentTimeMillis();
            System.out.println("花费时间："+ (end-start));
        }
        grabber.close();
    }

    /**
     * BufferedImage转byte[]
     *
     * @param bImage BufferedImage对象
     * @return byte[]
     * @auth zhy
     */
    private static byte[] imageToBytes(BufferedImage bImage) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            ImageIO.write(bImage, "png", out);
            return out.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        snapshot("rtmp://10.101.236.143/live/livestream2");
    }
}
