package com.huanke.iot.manage.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public final class FileUtil {
    public static int getFileLength(String url1) {
        int length;
        URL url;
        try {
            url = new URL(url1);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            //根据响应获取文件大小
            length = urlcon.getContentLength();
            urlcon.disconnect();
        } catch (MalformedURLException e) {
            return 0;
        } catch (IOException e) {
            return 0;
        }
        return length;
    }
}
