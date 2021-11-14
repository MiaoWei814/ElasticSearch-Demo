package com.miao.apidemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miao.apidemo.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchExtBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class ApiDemoApplicationTests {
    private static final String MIAO_INDEX = "miao_index";
    //按类型进行查找,也就是 RestHighLevelClient这种类型去搜索,
    //类型如果出现多个就会搜索对应的名称,如果需要指定名称的话我们可以用@Qualifer来指定名称
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 测试创建索引
     *
     * @throws IOException ioexception
     */
    @Test
    void testCreateIndex() throws IOException {
        //1.创建索引请求
        CreateIndexRequest indexRequest = new CreateIndexRequest(MIAO_INDEX);
        //2.客户端执行创建请求 IndicesClient,   请求后返回响应
        CreateIndexResponse response = restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
        System.out.println("response = " + response);
    }

    /**
     * 测试获取索引
     *
     * @throws IOException ioexception
     */
    @Test
    void testExistsIndex() throws IOException {
        GetIndexRequest miao_index = new GetIndexRequest(MIAO_INDEX);
        boolean exists = restHighLevelClient.indices().exists(miao_index, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 测试删除索引
     *
     * @throws IOException ioexception
     */
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest miao_index = new DeleteIndexRequest(MIAO_INDEX);
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(miao_index, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 测试添加文档
     *
     * @throws IOException ioexception
     */
    @Test
    void testAddDocument() throws IOException{
        //创建对象
        User user = new User("miaowei", 3);
        //创建索引请求
        IndexRequest indexRequest = new IndexRequest(MIAO_INDEX);

        //规则 put/miao_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");

        //将我们的数据放入请求 转为json
        indexRequest.source(JSONObject.toJSONString(user), XContentType.JSON);

        //客户端发送请求,获取响应的结果
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(index.toString());
        System.out.println(index.status());//执行状态 created

    }

    /**
     * 测试获取文档,判断是否存在
     *
     * @throws IOException ioexception
     */
    @Test
    void testIsExistsDocument() throws IOException{
        //命令:GET Index/_doc/1
        GetRequest getRequest = new GetRequest(MIAO_INDEX, "1");
        //不获取返回的 _source的上下文了
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        //排序字段
        getRequest.storedFields("_none_");

        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("exists = " + exists);

    }

    /**
     * 测试获取文档
     *
     * @throws IOException ioexception
     */
    @Test
    void testGetDocument() throws IOException{
        //命令:GET Index/_doc/1
        GetRequest getRequest = new GetRequest(MIAO_INDEX, "1");
        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documentFields); //这里返回全部内容和命令是一样的!
        System.out.println(documentFields.getSource());
        System.out.println(documentFields.getVersion());
        System.out.println(documentFields.getType());


    }

    /**
     * 测试更新文档
     *
     * @throws IOException ioexception
     */
    @Test
    void testUpdateDocument() throws IOException{
        //创建一个更新请求
        UpdateRequest updateRequest = new UpdateRequest(MIAO_INDEX, "1");
        updateRequest.timeout("1s"); //设置最大超时时间

        User user = new User("缪威很菜", 19);
        //放入到更新请求中,设置为JSON文档类型
        updateRequest.doc(JSONObject.toJSONString(user), XContentType.JSON);

        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update);
        System.out.println(update.status());

    }

    /**
     * 测试删除请求
     *
     * @throws IOException ioexception
     */
    @Test
    void testDeleRequest() throws IOException{
        //创建一个删除请求
        DeleteRequest deleteRequest = new DeleteRequest(MIAO_INDEX, "1");
        //添加删除超时时间,过了时间就不删除了
        deleteRequest.timeout("1s");

        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
        System.out.println(delete.status());

    }

    /**
     * 批量插入数据
     *
     * @throws Exception 异常
     */
    @Test
    public void testBLukRequest() throws Exception{
        //在真实的项目开发中有一种可能就是从数据库中导入,那么就需要批量的插入
        //如果直接用for循环那么也是可以的,但是人家已经为我们提供好了API
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueMinutes(1));

        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("缪威1", 1));
        userList.add(new User("缪威2", 2));
        userList.add(new User("缪威3", 3));
        userList.add(new User("缪威4", 4));
        userList.add(new User("缪威4", 4));
        userList.add(new User("缪威5", 5));
        userList.add(new User("缪威6", 6));
        userList.add(new User("缪威7", 7));

        //批处理请求
        for (int i = 0; i < userList.size(); i++) {
            //批量更新和批量删除就在这里修改对应的请求即可,
             bulkRequest.add(new IndexRequest(MIAO_INDEX)
                        .id("" + (i + 1)) //不写id就会为我们随机生成一个id
                        .source(JSONObject.toJSONString(userList.get(i)), XContentType.JSON));
        }

        //执行批量操作
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk);
        System.out.println(bulk.hasFailures()); //是否失败,返回false表示成功,就是没有失败就是成功了
        System.out.println(bulk.status());
    }

    /**
     * 查询
     * SearchRequest 搜索请求
     * SearchSourceBuilder 条件构造
     * HighlightBuilder 构建高亮
     * TermQueryBuilder 精确查询
     * MatchAllQueryBuilder
     * 总之xxxQueryBuilder 对应着xxx功能
     *
     * @throws Exception 异常
     */
    @Test
    public void testSearch() throws Exception{
        //构建一个搜索请求
        SearchRequest searchRequest = new SearchRequest(MIAO_INDEX);
        //构建搜索条件,比如排序、高亮等等;所有的资源都是通过这个构建器进行构建的
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //查询--查询条件,我们可以通过QueryBuilders工具类来实现
            //termQuery精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "miaowei");
//        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();  匹配所有
        builder.query(termQueryBuilder);
        //设置最大查询时间
        builder.timeout(TimeValue.timeValueSeconds(60));
        //设置分页,这里我们可以不用设置参数,因为它自带了一些默认的配置
//        builder.from();
//        builder.size();

        //把构建的参数放在搜索请求里面
        searchRequest.source(builder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(search.getHits()));
        System.out.println("========================================================");
        //第一个Hits是整个对象,第二个是具体的文档对象,就是里面有具体的参数信息
        for (SearchHit hit : search.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
}
