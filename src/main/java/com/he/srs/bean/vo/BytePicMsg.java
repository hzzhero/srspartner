package com.he.srs.bean.vo;

import io.protostuff.ByteString;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class BytePicMsg extends BaseMsg{

    private ByteString imageData;

    private String picID;

    private int imgWidth;

    private int imgHeight;

    private long stride;

    private int imageFormat;

    private long timestamp;

    public BytePicMsg() {
        super();
    }

    public BytePicMsg(ByteString imageData, String picID, int imgWidth, int imgHeight, long stride, int imageFormat, long timestamp) {
        this.imageData = imageData;
        this.picID = picID;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.stride = stride;
        this.imageFormat = imageFormat;
        this.timestamp = timestamp;
    }
}
