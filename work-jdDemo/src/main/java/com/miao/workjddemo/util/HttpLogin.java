package com.miao.workjddemo.util;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.Date;


/**
 * @author zeze
 * @ClassName: HttpLogin
 * @Description: java通过httpclient获取cookie模拟登录
 * @date 2015年11月10日 下午4:18:08
 */

public class HttpLogin {

    public static void main(String[] args) throws IOException {
        //时间戳
        long timestamp = DateUtil.currentSeconds();
        //请求地址
        String url = "http://api.qian-gua.com/login/Login?_=" + timestamp;
        HttpClient client = new HttpClient();
        //post请求方式
        PostMethod postMethod = new PostMethod(url);
        //推荐的数据存储方式,类似key-value形式
        NameValuePair telPair = new NameValuePair();
        telPair.setName("tel");
        telPair.setValue("15882688721");
        NameValuePair pwdPair = new NameValuePair("pwd", "2000814miaowei");
        //封装请求参数
        postMethod.setRequestBody(new NameValuePair[]{telPair, pwdPair});
        //这里是设置请求内容为json格式,根据站点的格式决定
        //因为这个网站会将账号密码转为json格式,所以需要这一步
        postMethod.setRequestHeader("Content_Type", "application/json");
        //执行请求
        client.executeMethod(postMethod);
        //通过Post/GetMethod对象获取响应头信息
        String cookie = postMethod.getResponseHeader("Cookie").getValue();
        //截取需要的内容
        String sub = cookie.substring(cookie.indexOf("&"), cookie.lastIndexOf("&"));
        String[] splitPwd = sub.split("=");
        String pwd = splitPwd[1];
        System.out.println(pwd);
    }
}