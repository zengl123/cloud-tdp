package com.zenlin.cloud.tdp.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.interceptor.annotations.After;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.zenlin.cloud.tdp.entity.EsEntity;
import com.zenlin.cloud.tdp.entity.RestMessage;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.cluster.Health;
import io.searchbox.cluster.NodesInfo;
import io.searchbox.cluster.NodesStats;
import io.searchbox.core.*;
import io.searchbox.indices.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/15  9:44.
 */
@Component
public class EsUtils<T> {

    private final Logger LOGGER = LoggerFactory.getLogger(EsUtils.class);

    private static JestClient jestClient;

    @Autowired
    private ResultUtil resultUtil;

    private final Integer PAGE_NUMBER = 0;
    private final Integer PAGE_SIZE = 10000;


    public EsUtils() {
        getJestClientFactory("http://localhost:9200");
    }

    @Before
    public void getJestClientFactory(String url) {
        if (jestClient == null) {
            JestClientFactory jestClientFactory = new JestClientFactory();
            jestClientFactory.setHttpClientConfig(
                    new HttpClientConfig.Builder(url)
                            .multiThreaded(true)
                            .defaultMaxTotalConnectionPerRoute(5)
                            .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create())
                            .maxTotalConnection(10).build());
            jestClient = jestClientFactory.getObject();
        }
    }

    /**
     * 判断索引目录是否存在
     *
     * @throws Exception
     */
    private boolean indicesExists(String indexName) throws Exception {
        IndicesExists indicesExists = new IndicesExists.Builder(indexName).build();
        JestResult result = jestClient.execute(indicesExists);
        return result.isSucceeded();
    }

    /**
     * 清缓存
     *
     * @throws Exception
     */
    private boolean clearCache() throws Exception {
        ClearCache closeIndex = new ClearCache.Builder().build();
        JestResult result = jestClient.execute(closeIndex);
        System.out.println(result.getJsonString());
        return result.isSucceeded();
    }


    /**
     * 关闭索引
     *
     * @throws Exception
     */
    private boolean closeIndex(String indexName) throws Exception {
        CloseIndex closeIndex = new CloseIndex.Builder(indexName).build();
        JestResult result = jestClient.execute(closeIndex);
        System.out.println(result.getJsonString());
        return result.isSucceeded();
    }

    /**
     * 优化索引
     *
     * @throws Exception
     */
    private boolean optimize() throws Exception {
        Optimize optimize = new Optimize.Builder().build();
        JestResult result = jestClient.execute(optimize);
        System.out.println(result.getJsonString());
        return result.isSucceeded();
    }

    /**
     * 刷新索引
     *
     * @throws Exception
     */
    private boolean flush() throws Exception {
        Flush flush = new Flush.Builder().build();
        JestResult result = jestClient.execute(flush);
        System.out.println(result.getJsonString());
        return result.isSucceeded();
    }

    /**
     * 查看节点信息
     *
     * @throws Exception
     */
    private void nodesInfo() throws Exception {
        NodesInfo nodesInfo = new NodesInfo.Builder().build();
        JestResult result = jestClient.execute(nodesInfo);
        System.out.println(result.getJsonString());
    }

    /**
     * 查看集群健康信息
     *
     * @throws Exception
     */
    private void health() throws Exception {
        Health health = new Health.Builder().build();
        JestResult result = jestClient.execute(health);
        System.out.println(result.getJsonString());
    }

    /**
     * 节点状态
     *
     * @throws Exception
     */
    private void nodesStats() throws Exception {
        NodesStats nodesStats = new NodesStats.Builder().build();
        JestResult result = jestClient.execute(nodesStats);
        System.out.println(result.getJsonString());
    }

    /**
     * 获取Document
     *
     * @param index
     * @param type
     * @param id
     * @throws Exception
     */
    private T getDocument(String index, String type, String id, Class<T> t) throws Exception {
        Get get = new Get.Builder(index, id).type(type).build();
        JestResult result = jestClient.execute(get);
        return result.getSourceAsObject(t);
    }


    /**
     * 查询全部
     *
     * @throws Exception
     */
    public List<T> searchAll(String indexName, String type, Integer pageNumber, Integer pageSize, Class<T> t) throws Exception {
        if (null == pageNumber) {
            pageNumber = PAGE_NUMBER;
        }
        if (null == pageSize) {
            pageSize = PAGE_SIZE;
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from(pageNumber);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(indexName)
                .addType(type)
                .build();
        SearchResult result = jestClient.execute(search);
        List<SearchResult.Hit<T, Void>> hits;
        List<T> list = new ArrayList<>();
        try {
            hits = result.getHits(t);
            for (SearchResult.Hit<T, Void> hit : hits) {
                T source = hit.source;
                list.add(source);
            }
        } catch (NullPointerException e) {
            LOGGER.error("数据不存在!");
        }
        return list;
    }


    /**
     * 批量插入数据
     *
     * @param
     */
    public RestMessage saveEntityBatch(EsEntity<T> esEntity) throws IOException {
        Bulk.Builder bulk = new Bulk.Builder();
        for (T entity : esEntity.getEntityList()) {
            Index index = new Index.Builder(entity).index(esEntity.getIndexName()).type(esEntity.getType()).build();
            bulk.addAction(index);
        }
        JestResult jestResult = jestClient.execute(bulk.build());
        boolean succeeded = jestResult.isSucceeded();
        List<String> list = new ArrayList<>();
        if (succeeded) {
            String sourceAsString = jestResult.getJsonString();
            JSONObject jsonObject = JSON.parseObject(sourceAsString);
            JSONArray items = jsonObject.getJSONArray("items");
            for (int i = 0; i < items.size(); i++) {
                JSONObject object = items.getJSONObject(i);
                JSONObject index = object.getJSONObject("index");
                String id = index.getString("_id");
                list.add(id);
            }
            return resultUtil.success(list, "SUCCESS");
        } else {
            return resultUtil.error("ERROR");
        }
    }


    /**
     * 批量插入数据
     *
     * @param
     */
    public RestMessage updateEntityBatch(EsEntity<T> esEntity) throws IOException {
        Bulk.Builder bulk = new Bulk.Builder();
        for (T entity : esEntity.getEntityList()) {
            Update update = new Update.Builder(entity).index(esEntity.getIndexName()).type(esEntity.getType()).id(esEntity.getId()).build();
            bulk.addAction(update);
        }
        JestResult jestResult = jestClient.execute(bulk.build());
        boolean succeeded = jestResult.isSucceeded();
        return resultUtil.success(succeeded, "SUCCESS");
    }

    /**
     * 单个添加数据
     *
     * @param
     */
    public RestMessage saveEntityOne(EsEntity<T> esEntity) throws IOException {
        Index index = new Index.Builder(esEntity.getEntity()).index(esEntity.getIndexName()).type(esEntity.getType()).build();
        JestResult jestResult = jestClient.execute(index);
        String jsonString = jestResult.getJsonString();
        return resultUtil.success(jsonString, "SUCCESS");
    }

    public RestMessage updataEntityOne(EsEntity<T> esEntity) throws IOException {
        Update update = new Update.Builder(esEntity.getEntity()).index(esEntity.getIndexName()).type(esEntity.getType()).id(esEntity.getId()).build();
        JestResult jestResult = jestClient.execute(update);
        String jsonString = jestResult.getJsonString();
        return resultUtil.success(jsonString, "SUCCESS");
    }

    /**
     * 根据索引名称删除索引
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public RestMessage deletedByIndexName(String indexName) throws IOException {
        DeleteIndex deleteIndex = new DeleteIndex.Builder(indexName).build();
        JestResult jestResult = jestClient.execute(deleteIndex);
        boolean succeeded = jestResult.isSucceeded();
        return resultUtil.success(succeeded, "SUCCESS");
    }

    /**
     * 根据type删除type下所有doc数据
     *
     * @param indexName
     * @param type
     * @return
     * @throws IOException
     */
    /*public RestMessage deletedByType(String indexName, String type) throws IOException {
        String deleteByQuery = "{\"query\": {\"match_all\": {}}}";
        boolean b = deleteByQuery(indexName, type, deleteByQuery);
        return resultUtil.success(b, "SUCCESS");
    }

    public boolean deleteByQuery(String indexName, String type, String query) throws IOException {
        DeleteByQuery deleteByQuery = new DeleteByQuery.Builder(query)
                .addIndex(indexName)
                .addType(type)
                .build();
        JestResult execute = jestClient.execute(deleteByQuery);
        return execute.isSucceeded();
    }*/

    /**
     * 删除Document
     *
     * @param index
     * @param type
     * @param id
     * @throws Exception
     */
    public String deleteDocumentById(String index, String type, String id) throws IOException {
        Delete delete = new Delete.Builder(id).index(index).type(type).build();
        JestResult result = jestClient.execute(delete);
        if (result.isSucceeded()) {
            return id;
        } else {
            return null;
        }
    }


    /**
     * 根据条件查询数据
     *
     * @param searchSourceBuilder
     * @param indexName
     * @param type
     * @return
     */
    private SearchResult queryByCondition(SearchSourceBuilder searchSourceBuilder, String indexName, String type) throws IOException {
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(indexName)
                .addType(type)
                .build();
        SearchResult searchResult = jestClient.execute(search);
        return searchResult;
    }

    /**
     * 根据指定字段精确查询为指定类
     *
     * @param
     */
    public List<T> findEntity(String queryCondition, String condition, String indexName, String type, Class<T> t) throws IOException {
        SearchResult searchResult = findSearchResult(queryCondition, condition, indexName, type);
        List<T> list = parseToEntity(searchResult, t);
        return list;
    }

    /**
     * 解析返回结果为指定实体类
     *
     * @param
     */
    private List<T> parseToEntity(SearchResult searchResult, Class<T> t) {
        List<SearchResult.Hit<T, Void>> listT = searchResult.getHits(t);
        List<T> list = new ArrayList<>();
        if (listT != null && listT.size() > 0) {
            for (SearchResult.Hit<T, Void> tVoidHit : listT) {
                list.add(tVoidHit.source);
            }
        }
        return list;
    }

    /**
     * 根据指定字段精确查询
     *
     * @param condition
     */
    public SearchResult findSearchResult(String queryCondition, String condition, String indexName, String type) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery(queryCondition, condition));
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(indexName)
                .addType(type)
                .build();
        SearchResult searchResult = jestClient.execute(search);
        return searchResult;
    }


    @After
    public void tearDown() throws Exception {
        closeJestClient(jestClient);
    }

    /**
     * 关闭JestClient客户端
     *
     * @param jestClient
     * @throws Exception
     */
    public void closeJestClient(JestClient jestClient) throws Exception {
        if (jestClient != null) {
            jestClient.shutdownClient();
        }
    }
}
