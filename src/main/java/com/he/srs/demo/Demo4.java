package com.he.srs.demo;

import org.bytedeco.javacv.*;

import javax.swing.*;

/**
 * 过滤器应用
 */
public class Demo4 {

        public static void test() throws Exception, InterruptedException{

            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);//新建opencv抓取器，一般的电脑和移动端设备中摄像头默认序号是0，不排除其他情况
            grabber.start();//开始获取摄像头数据

            CanvasFrame canvas = new CanvasFrame("摄像头预览");//新建一个预览窗口
            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //窗口是否关闭
            while(canvas.isDisplayable()){
                /*获取摄像头图像并在窗口中显示,这里Frame frame=grabber.grab()得到是解码后的视频图像*/
                canvas.showImage(grabber.grab());
            }
            grabber.close();//停止抓取
        }

        /**
         * 使用字库支持简体中文字符水印
         * @param filterContent
         */
        public static void overlayChar(String filterContent) throws Exception, org.bytedeco.javacv.FrameFilter.Exception {

            OpenCVFrameGrabber cameraGrabber = new OpenCVFrameGrabber(0);
//            FFmpegFrameGrabber cameraGrabber = new FFmpegFrameGrabber("video=Integrated Camera");
            cameraGrabber.setFormat("dshow");// 基于dshow的方式的摄像头采集
            cameraGrabber.start();

            int width=cameraGrabber.getImageWidth(),height=cameraGrabber.getImageHeight();

            //设置过滤器内容，具体参考http://ffmpeg.org/ffmpeg-filters.html
            FFmpegFrameFilter filter =new FFmpegFrameFilter(filterContent,width,height);
            filter.start();

            //FFmpegFrameFilter流程是初始化设置好过滤器后start()-->push(frame)-->Frame pull()--> stop()
            //使用push()把音视频帧推送到过滤器，然后通过pull()取出视频帧
            //一般的使用流程是grabber.grab得到的数据push进过滤器中，然后通过pull取出过滤器处理过的视频帧即可。

            CanvasFrame canvas = new CanvasFrame("画面预览");// 新建一个预览窗口
            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvas.setAlwaysOnTop(true);
            Frame screenFrame = null,cameraFrame;

            // 抓取屏幕画面
            for (; canvas.isShowing()&&(cameraFrame=cameraGrabber.grab())!=null; ) {
                //把抓取到的摄像头画面塞进过滤器
                filter.push(cameraFrame);

                //取出过滤器合并后的图像
                Frame filterFrame=filter.pullImage();

                // 显示过滤器处理后的画面
                canvas.showImage(filterFrame);
            }
            cameraGrabber.close();
            filter.close();
            canvas.dispose();
        }

    public static void test2() throws Exception {
        String moveRCharFilterContent="delogo=0:0:220:90:1:1";
        overlayChar(moveRCharFilterContent);

    }

    public static void main(String[] args) {
        try {
            test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
