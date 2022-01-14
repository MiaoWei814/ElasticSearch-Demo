package com.miao.workjddemo.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2022-01-11 15:24
 **/
public class TbDetailHtmlParseTest {
    public static void main(String[] args) throws IOException {
        String url = "https://detail.tmall.com/item.htm?spm=a1z10.5-b-s.w4011-22553656875.51.1a127305muD9zh&id={}&rn=40f78a2af9164610f47255f50f1bd9db&abbucket=5";
        // 动态模拟请求数据
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(CharSequenceUtil.format(url, "658738258699"));
        // 模拟浏览器浏览（user-agent的值可以通过浏览器浏览，查看发出请求的头文件获取）
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
        httpGet.setHeader("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
        httpGet.setHeader("Connection", "Keep-Alive");
        httpGet.setHeader("Referer", "https://beoka.tmall.com/category-1304329207.htm");
        httpGet.setHeader("Cookie", "isg=BK-miaowei-vcmQBEvpk4yj7DhagoZMwPcO5VAN2isfkycE8cZ4nEM8SySDjx9DRkIAuc9vu; l=eBQP-FAeO9s4y2eZBOfahurza77OSIOYYuPzaNbMiOCP_7CB5vg5W6pKPa86C31Vh6kvR3rZNUQJBeYBqQAonxvtJ6S87Pkmn; tfstk=c4KFBjf6LDnF1osAxMsrPUI14GpdwmJHElWf-e6_Vz0y9Ofc47BMtqWNFznR-; cna=wh49GBDodiUCAd7RgH0sZApH; xlly_s=1; cq=ccp%3D1; dnk=%5Cu5A01%5Cu54E5%5Cu6709%5Cu70B9%5Cu53FC; uc1=existShop=false&pas=0&cookie16=URm48syIJ1yk0MX2J7mAAEhTuw%3D%3D&cookie15=VT5L2FSpMGV7TQ%3D%3D&cookie14=UoewAe0Mh%2FtjSA%3D%3D&cookie21=VFC%2FuZ9aiKCaj7AzMHh1; uc3=id2=UU8…u5A01%5Cu54E5%5Cu6709%5Cu70B9%5Cu53FC; cookie1=BxEwMvh8EVi7LPGhPyrHAZhKUurqZYGTRoEOx9tG8HY%3D; login=true; cookie17=UU8K0xzcPv%2F%2Fxw%3D%3D; cookie2=1590b4b687bf82e95169eeefcd591d81; _nk_=%5Cu5A01%5Cu54E5%5Cu6709%5Cu70B9%5Cu53FC; sgcookie=E100bNwHyqg3KmwEux%2FZoIpbTAJmkjFGF21T7V6vfnogOiPj%2FnLijkEumL4CGzvVFVKKcQyPS5p8nQ2RQK9K38fc1HboiuhwSyvs%2FFd18x%2BDVKD2RoBFjTQCXdHV%2BEhtE6V5; cancelledSubSites=empty; sg=%E5%8F%BC05; t=4beb951a89b428cdd8b2bc3517007106; csg=d0306959; _tb_token_=13b78e583a55; pnm_cku822=");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity, Consts.UTF_8);
        //替换所有带有html占位符
        String replace = html.replace("\\", "");
        //开始解析为html
        Document document = Jsoup.parse(replace);
        //元素节点
        Element element = document.getElementsByClass("tb-wrap").get(0);

        //主描述
        String mainDescription = element.select(".tb-detail-hd h1").text();
        //子描述
        String sonDescription = element.select(".tb-detail-hd p").text();
        //旧价格
//        String text = element.getElementsByClass("tm-fcs-panel").get(0).getElementsByTag("dd").text();

        //左边小图片信息
        List<String> imgInfo = new ArrayList<>();
        Elements imgSelect = Objects.requireNonNull(document.getElementById("J_UlThumb")).select("li");
        imgSelect.forEach(x -> imgInfo.add(x.getElementsByTag("img").attr("src")));

        //详情
        List<String> shopDetail = new ArrayList<>();
        document.getElementById("J_AttrUL").getElementsByTag("li").forEach(x -> shopDetail.add(sonDescription));

        //获取详情页中的详情图片地址
        Elements scriptImgs = document.getElementsByTag("script");
        //组织拼接视频地址
        String formatVideoUrl = "https://cloud.video.taobao.com/play/u/{}/p/1/e/6/t/1/{}.mp4";
        for (Element script : scriptImgs) {
            String html1 = script.html();
            String str = "TShop.Setup(";
            if (html1.contains(str)) {
                String str1 = html1.substring(0, html1.indexOf(str) + str.length());
                //替换字符串
                String replace1 = html1.replace(str1, "");
                String replaceImgSrc = replace1.substring(0, replace1.indexOf(")"));
                //解析为json
                JSONObject parseObject = JSONObject.parseObject(replaceImgSrc);
                //获取到详情图片
                String shopDetailImgUrl = JSONObject.parseObject(parseObject.getString("api")).getString("httpsDescUrl");
                //获取之前的价格
                String oldPrice = JSONObject.parseObject(parseObject.getString("detail")).getString("defaultItemPrice");
                //获取到库存（不用，已经可以获取到总库存和对应sky的库存）
//                String quantity = JSONObject.parseObject(parseObject.getString("itemDO")).getString("quantity");
//                System.out.println(imgInfo);
                //获取到左边视频
                String videoUrl = parseObject.getJSONObject("itemDO").getString("imgVedioID");
                if (CharSequenceUtil.isNotBlank(videoUrl)) {
                    String urderId = parseObject.getJSONObject("itemDO").getString("userId");
                    String leftVideoUrl = CharSequenceUtil.format(formatVideoUrl, urderId, videoUrl);
                    System.out.println(leftVideoUrl);
                }


                //颜色分类
                JSONObject valItemInfo = JSONObject.parseObject(parseObject.getString("valItemInfo"));
                List<SkuList> skuList = JSONObject.parseArray(valItemInfo.getString("skuList"), SkuList.class);
                //map以“;1627207:28332;”
                Map skuMap = valItemInfo.getObject("skuMap", Map.class);
                //具体的sku对应的图
                Map propertyPics = parseObject.getObject("propertyPics", Map.class);

                //循环遍历组装
                //propertyPics为key去找skumap的key，然后以skumap的key去找skulist的id
                List<SkuDetail> skuDetails = new ArrayList<>();
                Map<String, SkuList> skuListMap = skuList.stream().collect(Collectors.toMap(SkuList::getSkuId, sku -> sku));
                for (Object mapKey : propertyPics.keySet()) {
                    String key = mapKey.toString();
                    if (key.contains("default")) {
                        //获取到左边小图变大图
                        List<String> leftBigImg = JSONObject.parseArray(propertyPics.get(key).toString(), String.class);
                        continue;
                    }
                    SkuDetail detail = new SkuDetail();
                    //获取本身
                    List<String> skuImg = JSONObject.parseArray(propertyPics.get(key).toString(), String.class);
                    detail.setImgUrl(skuImg.get(0));
                    //获取skumap
                    SkuMap map = JSONObject.parseObject(skuMap.get(key).toString(), SkuMap.class);
                    detail.setPriceCent(map.getPriceCent());
                    detail.setPrice(map.getPrice());
                    detail.setStock(map.getStock());
                    //获取skulist
                    SkuList sku = skuListMap.get(map.getSkuId());
                    detail.setNames(sku.getNames());
                    detail.setPvs(sku.getPvs());
                    detail.setSkuId(sku.getSkuId());
                    skuDetails.add(detail);

                }

//                System.out.println(skuDetails);
            }
        }
    }
}
