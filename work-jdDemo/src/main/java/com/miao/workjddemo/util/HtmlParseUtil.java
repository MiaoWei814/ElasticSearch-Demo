package com.miao.workjddemo.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.miao.workjddemo.pojo.ParseSource;
import io.netty.util.CharsetUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2021-11-15 20:01
 **/
@Component
public class HtmlParseUtil {

    /**
     * 解析jdbook
     *
     * @return {@link List}<{@link ParseSource}>
     */
    public List<ParseSource> parseJDBook(String keyword) {
        List<ParseSource> result = new ArrayList<>();
        if (CharSequenceUtil.isEmpty(keyword)) {
            throw new RuntimeException("keyword参数不能为空!");
        }
        //因为在获取请求的时候路径URL不允许需要带有空格
        keyword = CharSequenceUtil.cleanBlank(keyword);

        try {
            //获取请求
            //注意:需要联网 并且不能获取到AJAX, 这里enc是为了传输中文而不乱码
            String url = CharSequenceUtil.format("https://search.jd.com/Search?keyword={}&enc={}", keyword, CharsetUtil.UTF_8);
            //解析网页(Jsoup返回Document就是浏览器Document对象)
            Document document = Jsoup.parse(new URL(url), 3000);
            //所有能在js中使用的方法,这里都能进行使用
            Element goodsList = document.getElementById("J_goodsList");
            //获取所有的li元素
            Elements liElementsByTag = goodsList.getElementsByTag("li");
            //获取元素中的内容,这里el就是每一个li标签
            ParseSource source;
            for (Element el : liElementsByTag) {
                source = new ParseSource();
                //获取标题
                String context = el.getElementsByClass("p-name").get(0).text();
                //价格
                String price = el.getElementsByClass("p-price").get(0).text();
                //图片
                String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
                //获取店铺名
                String shopName = el.getElementsByClass("p-shopnum").text();

                source.setParseContext(context).setParsePrice(price).setParseImg(img).setParseShopName(shopName);
                result.add(source);
            }
        } catch (IOException e) {
            return result;
        }
        return result;
    }
}
