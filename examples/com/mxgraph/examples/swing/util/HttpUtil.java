package com.mxgraph.examples.swing.util;

import com.alibaba.fastjson.TypeReference;
import com.mxgraph.examples.swing.db.TelemetryDTO;
import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.util.List;

/**
 * @Author: HUHU
 * @Date: 2019/7/15 14:52
 */
public class HttpUtil {

    private String url;

    public HttpUtil(String url){
        this.url = url;
    }

    public List<TelemetryDTO> getTelemetryDTOList() {
        // http请求获取json字符串
        String str = getDateByUrl(url);
        System.out.println("json:  "+str);
        // Json字符串转换转化为list
        return JSON.parseObject(str, new TypeReference<List<TelemetryDTO>>() {
        });
    }

   // public String

    /**
     * 根据URL试用get方法取得返回的数据
     *
     * @param url URL地址，参数直接挂在URL后面即可
     * @return
     */
    public  String getDateByUrl(String url) {

        String data = null;
        //构造HttpClient的实例
        HttpClient httpClient = new HttpClient();
        //创建GET方法的实例
        GetMethod getMethod = new GetMethod(url);
        //设置头信息：如果不设置User-Agent可能会报405，导致取不到数据
        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:39.0) Gecko/20100101 Firefox/39.0");
        //使用系统提供的默认的恢复策略
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        try {
            //开始执行getMethod
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed:" + getMethod.getStatusLine());
            }
            //读取内容
            byte[] responseBody = getMethod.getResponseBody();
            //处理内容
            data = new String(responseBody);
        } catch (HttpException e) {
            //发生异常，可能是协议不对或者返回的内容有问题
            System.out.println("Please check your provided http address!");
            data = "";
            e.printStackTrace();
        } catch (IOException e) {
            //发生网络异常
            data = "";
            e.printStackTrace();
        } finally {
            //释放连接
            getMethod.releaseConnection();
        }
        return data;
    }
}
