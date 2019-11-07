package com.hua.service.impl;

import com.hua.service.SearchService;
import com.hua.utils.ESUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


/**
 * maven09_elasticsearch
 * 2019-11-06 14:57
 * Author: JokerHua
 * ******************
 * 【文档注释】:
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    private Logger logger= LoggerFactory.getLogger(SearchServiceImpl.class);

    private static final String indexName="bookstore";//库
    private static final String indexType="books";//表
    @Override
    public void createIndex(String indexName) throws Exception{
        //1.创建请求
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        //2.构建系统设置  分片，副本
        ESUtils.buildSettings(request);
        ESUtils.buildMappings(request,"books");

        CreateIndexResponse response=restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(response.isAcknowledged());
    }

    @Override
    public void deleteIndex(String indexName)  throws Exception{
        DeleteIndexRequest request = new DeleteIndexRequest();
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        logger.info(indexName+"删除成功与否："+response.isAcknowledged());

    }

    @Override
    public void addData(String json)  throws Exception{
        IndexRequest request = new IndexRequest(indexName, indexType);
        request.source(json, XContentType.JSON);

        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        logger.info("新增："+indexResponse.status());
    }

    @Override
    public boolean existIndex(String indexName) throws Exception {
        //1.创建请求对象
        GetIndexRequest request=new GetIndexRequest();
        request.indices(indexName);
        //2.判断
        boolean f=restHighLevelClient.indices().exists(request,RequestOptions.DEFAULT);
        logger.info(indexName+"是否存在："+f);
        return f;
    }

    @Override
    public void saveOrUpdate(String json, int id) throws Exception {
        IndexRequest request = new IndexRequest(indexName, indexType, id + "");
        request.source(json,XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        logger.info("新增/修改："+indexResponse.status());
    }

    @Override
    public void deleteById(String id) throws Exception {
        DeleteRequest request = new DeleteRequest(indexName, indexType, id);
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        logger.info("根据id删除document："+response.status());
    }

    @Override
    public void queryAll() throws Exception {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(indexName);//索引

        SearchResponse response=restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        /**
         * "hits": {
         *     "total": 9,
         *     "max_score": 1,
         *     "hits": [
         *       {}]
         */
        SearchHits searchHits=response.getHits();

        //总条数
        long totalHits = searchHits.getTotalHits();
        System.out.println("total:"+totalHits);

        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Override
    public void matchQuery(String keyword) throws Exception {
        //1.搜索请求对象
        SearchRequest searchRequest=new SearchRequest();
        //2.索引①
        searchRequest.types(indexName);
        //3.查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //4.QueryBuilders.~ 指定查询类型
        //******不一样的查询就是这个不一样******
        searchSourceBuilder.query(QueryBuilders.matchQuery("title",keyword));
        //②
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        /**
         * "hits": {
         *     "total": 9,
         *     "max_score": 1,
         *     "hits": [
         *       {}]
         */
        //得到结果集
        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("total:"+totalHits);
        //遍历结果集
        SearchHit hit [] = hits.getHits();
        for (SearchHit searchHit:hit){
            System.out.println(searchHit.getSourceAsMap());
        }
    }

    @Override
    public void rangeQuery() throws Exception {
        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.rangeQuery("id").gte(1).lte(5));
        request.source(searchSourceBuilder);
        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Override
    public void fuzzyQuery(String keyword) throws Exception {
        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.fuzzyQuery("title",keyword));
        request.source(searchSourceBuilder);
        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Override
    public void sortById() throws Exception {
        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.sort("id", SortOrder.DESC);
        request.source(searchSourceBuilder);
        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Override
    public void queryByPage(int startIndex, int pageSize) throws Exception {
        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(pageSize);//pageSize
        searchSourceBuilder.from(startIndex);//起始记录

        request.source(searchSourceBuilder);
        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }
    }
    /**
     *  //组合查询
     *     //条件+范围+排序+分页+聚合+高亮
     * @param map
     * @throws Exception
     */
    @Override
    public void query(Map<String, Object> map) throws Exception {
        int startIndex = Integer.parseInt(map.get("startIndex") + "");
        int pageSize = Integer.parseInt(map.get("pageSize") + "");
        String keyword = map.get("keyword") + "";
        String  fuzzyWord = map.get("fuzzyWord")+"";

        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  databas

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //分页部分
        searchSourceBuilder.size(pageSize);//pageSize
        searchSourceBuilder.from(startIndex);//起始记录

        //查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("title",keyword));
        queryBuilder.must(QueryBuilders.rangeQuery("price").lte(2000).gte(1));//与  mustNot  非   should：或者
        //queryBuilder.must(QueryBuilders.fuzzyQuery("title",fuzzyWord));//与  mustNot  非   should：或者
        queryBuilder.should(QueryBuilders.termQuery("title","炼"));

        //排序
        searchSourceBuilder.sort("id",SortOrder.DESC);

        //高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");//高亮字段
        highlightBuilder.preTags("<em style=\"color:red;\">");
        highlightBuilder.postTags("</em>");

        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.query(queryBuilder);

        request.source(searchSourceBuilder);

        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println("非高亮"+hit.getSourceAsMap());

            Map<String, HighlightField> map1 = hit.getHighlightFields();
            System.out.println("高亮"+map1);

        }
    }
}
