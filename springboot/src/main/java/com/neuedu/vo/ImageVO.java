package com.neuedu.vo;

import lombok.Data;

@Data
public class ImageVO {
//图片名字
    private String uri;
//图片访问路径
    private String url;

    public ImageVO() {
    }
    public ImageVO(String uri, String url) {
        this.uri = uri;
        this.url = url;
    }
}
