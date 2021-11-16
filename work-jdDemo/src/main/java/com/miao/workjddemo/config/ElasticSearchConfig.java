package com.miao.workjddemo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2021-11-11 20:35
 **/
@Configuration
//@Configuration注解里面有一个@Compoent,表示这将注入到Spring容器中去
public class ElasticSearchConfig {

    /**
     * "@Bean":就好比是之前学习spring中xml配置文件:<beans id="restHighLevelClient" class="RestHighLevelClient"></beans>
     *
     * @return {@link RestHighLevelClient}
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        //构建客户端对象,如果是集群那么就new多个HttpHost
        return new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")));
    }
}
