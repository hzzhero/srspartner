package com.he.srs.util;

import com.he.srs.bean.vo.ResponseVo;

public class BuildResponseUtils {

    public static ResponseVo<?> success() {
        ResponseVo responseVo = new ResponseVo<>();
        responseVo.setCode(200);
        return responseVo;
    }

    public static  <T> ResponseVo<T> buildResponse(T topicInfo) {
        ResponseVo<T> responseVo = new ResponseVo<>();
        responseVo.setCode(200);
        responseVo.setData(topicInfo);
        return responseVo;
    }
}
