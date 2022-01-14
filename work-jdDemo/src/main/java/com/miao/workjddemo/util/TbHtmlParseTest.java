package com.miao.workjddemo.util;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2022-01-11 10:50
 **/
public class TbHtmlParseTest {
    public static void main(String[] args) throws IOException {
        List<TbShop> stbShops = new ArrayList<>();

        String url = "https://beoka.tmall.com/i/asynSearch.htm?_ksTS=1641867579793_136&callback=jsonp137&mid=w-22553656875-0&wid=22553656875&path=/category-1304329207.htm&catId=1304329207&scid=1304329207";
        // 动态模拟请求数据
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        // 模拟浏览器浏览（user-agent的值可以通过浏览器浏览，查看发出请求的头文件获取）
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
        httpGet.setHeader("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
        httpGet.setHeader("Connection", "Keep-Alive");
        httpGet.setHeader("Referer", "https://beoka.tmall.com/category-1304329207.htm");
        httpGet.setHeader("Cookie", "isg=BK-vcmQBEvpk4yj7DhagoZMwPcO5VAN2isfkycE8cZ4nEM8SySDjx9DRkIAuc9vu; l=eBQP-FAeO9s4y2eZBOfahurza77OSIOYYuPzaNbMiOCP_7CB5vg5W6pKPa86C31Vh6kvR3rZNUQJBeYBqQAonxvtJ6S87Pkmn; tfstk=c4KFBjf6LDnF1osAxMsrPUI14GpdwmJHElWf-e6_Vz0y9Ofc47BMtqWNFznR-; cna=wh49GBDodiUCAd7RgH0sZApH; xlly_s=1; cq=ccp%3D1; dnk=%5Cu5A01%5Cu54E5%5Cu6709%5Cu70B9%5Cu53FC; uc1=existShop=false&pas=0&cookie16=URm48syIJ1yk0MX2J7mAAEhTuw%3D%3D&cookie15=VT5L2FSpMGV7TQ%3D%3D&cookie14=UoewAe0Mh%2FtjSA%3D%3D&cookie21=VFC%2FuZ9aiKCaj7AzMHh1; uc3=id2=UU8…u5A01%5Cu54E5%5Cu6709%5Cu70B9%5Cu53FC; cookie1=BxEwMvh8EVi7LPGhPyrHAZhKUurqZYGTRoEOx9tG8HY%3D; login=true; cookie17=UU8K0xzcPv%2F%2Fxw%3D%3D; cookie2=1590b4b687bf82e95169eeefcd591d81; _nk_=%5Cu5A01%5Cu54E5%5Cu6709%5Cu70B9%5Cu53FC; sgcookie=E100bNwHyqg3KmwEux%2FZoIpbTAJmkjFGF21T7V6vfnogOiPj%2FnLijkEumL4CGzvVFVKKcQyPS5p8nQ2RQK9K38fc1HboiuhwSyvs%2FFd18x%2BDVKD2RoBFjTQCXdHV%2BEhtE6V5; cancelledSubSites=empty; sg=%E5%8F%BC05; t=4beb951a89b428cdd8b2bc3517007106; csg=d0306959; _tb_token_=13b78e583a55; pnm_cku822=");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        String html = EntityUtils.toString(entity, Consts.UTF_8);
        //替换所有带有html占位符
        String replace = html.replace("\\", "");
        //开始解析为html
        Document document = Jsoup.parse(replace);
        Elements prevAll = document.select(".pagination").prevAll();
        //获取所有的商品列表
        for (Element lineGoods : prevAll) {
            //获取一行商品
            Elements goods = lineGoods.select(".item");
            //单个商品
            for (int i = 0; i < goods.size(); i++) {
                TbShop shop = new TbShop();
                //商品id
                String goodsId = goods.get(i).attr("data-id");
                shop.setGoodsId(goodsId);

                //商品图
                String bigImg = goods.get(i).getElementsByClass("photo").get(0).getElementsByTag("img").attr("data-ks-lazyload");
                shop.setBigImg(bigImg);

                //商品小图
                List<String> smallImg = new ArrayList<>();
                Elements byTag = goods.get(i).select(".thumb-inner b");
                for (int x = 0; x < byTag.size(); x++) {
                    String src = byTag.get(x).getElementsByTag("img").attr("src");
                    smallImg.add(src);
                }
                shop.setSmallImg(smallImg);

                //描述
                String describe = goods.get(i).getElementsByClass("detail").get(0).getElementsByTag("a").text();
                shop.setDescribe(describe);

                Element detail = goods.get(i).getElementsByClass("detail").get(0).select(".attribute").get(0);
                //价格
                String price = detail.getElementsByClass("cprice-area").text();
                shop.setPrice(price);

                //总销量
                String sale = detail.getElementsByClass("sale-area").text();
                shop.setSale(sale);

                //评价
                String evaluate = "";
                Elements rates = goods.get(i).getElementsByClass("rates");
//                if (rates.size() != 0) evaluate = rates.get(0).getElementsByTag("a").text();
                evaluate = rates.get(0).getElementsByTag("a").text();
                shop.setEvaluate(evaluate);

                stbShops.add(shop);
            }
        }
//        Map<String, TbShop> collect = stbShops.stream().collect(Collectors.toMap(TbShop::getGoodsId, tbShop -> tbShop));
        System.out.println(stbShops);
    }
}
