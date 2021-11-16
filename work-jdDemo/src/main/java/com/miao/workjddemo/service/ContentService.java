package com.miao.workjddemo.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.miao.workjddemo.pojo.ParseSource;
import com.miao.workjddemo.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2021-11-15 22:13
 **/
@Service
public class ContentService {
    @Autowired
    private HtmlParseUtil htmlParseUtil;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 插入
     *
     * @param keyword 关键字
     * @return boolean
     * @throws IOException ioexception
     */
    public boolean insert(String keyword) throws IOException {
        //其实这里可以做一些判断,比如重复爬取,那么就会影响整个es的存储和查询
        //网页解析
        List<ParseSource> parseSources = htmlParseUtil.parseJDBook(keyword);
        //批量处理
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //id我就不指定,让他自己随机生成
        for (ParseSource item : parseSources) {
            bulkRequest.add(new IndexRequest()
                    .index("jd_books")
                    .source(JSONObject.toJSONString(item), XContentType.JSON));
        }
        //最后执行
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        //如果添加成功就是false,然后取反即可!
        return !bulk.hasFailures();
    }

    /**
     * 搜索
     *
     * @param orderNum   订单没有
     * @param orderSize 订单的大小
     * @param keyword   关键字
     */
    public List<ParseSource> search(Integer orderNum, Integer orderSize, String keyword) throws IOException {
        List<ParseSource> list = new ArrayList<>();
        if (orderNum == null || orderNum <= 0) {
            orderNum = 1;
        }

        //查询
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("jd_books");
        //构建查询器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //精准匹配
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("parseContext", keyword);
        builder.query(matchQueryBuilder);
        builder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //分页
        builder.from(orderNum);
        builder.size(orderSize);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("parseContext");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        builder.highlighter(highlightBuilder);

        searchRequest.source(builder);
        //开始搜索
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : search.getHits().getHits()) {
            //获取高亮字段
            HighlightField parseContext = hit.getHighlightFields().get("parseContext");
            //获取查询字段
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            ParseSource parseSource = BeanUtil.copyProperties(sourceAsMap, ParseSource.class);
            //修改为高亮的字段
            if (parseContext != null) {
                StringBuilder n_title= new StringBuilder();
                for (Text fragment : parseContext.getFragments()) {
                    n_title.append(fragment);
                }
                parseSource.setParseContext(n_title.toString());
            }
            list.add(parseSource);
        }
        return list;
    }
}
