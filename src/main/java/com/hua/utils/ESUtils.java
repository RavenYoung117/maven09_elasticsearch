package com.hua.utils;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;

/**
 * maven09_elasticsearch
 * 2019-11-06 15:35
 * Author: JokerHua
 * ******************
 * 【文档注释】:
 */

public class ESUtils {
    /**
     * --  创建索引
     * PUT shop
     * {
     *   "settings": {
     *     "number_of_shards": 3,
     *     "number_of_replicas": 2
     *
     *   }
     * }
     */
    public static void buildSettings(CreateIndexRequest request){
        request.settings(Settings.builder().put("index.number_of_shards",3));
        request.settings(Settings.builder().put("index.number_of_replicas",2));
    }

    /**
     * --   建立类型(table)
     *
     * PUT  /shop/_mapping/tb_item
     * {
     *   "properties": {
     *     "title":{
     *       "index": true,
     *       "store": true,
     *       "analyzer": "ik_smart",
     *       "type": "text"
     *     },
     *     "price":{
     *       "type": "double"
     *     },
     *     "sell_point":{
     *       "analyzer": "ik_smart",
     *       "type": "text"
     *     }
     *   }
     * }
     */
    public   static  void  buildMappings(CreateIndexRequest request,String typeName) throws  Exception{
        XContentBuilder xContentBuilder = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("title")
                .field("index",true)
                .field("store",true)
                .field("type","text")
                .field("analyzer","ik_smart")
                .endObject()

                .startObject("price")
                .field("type","double")
                .field("store",true)
                .endObject()

                .startObject("author")
                .field("type","keyword")
                .field("store",true)
                .endObject()

                .endObject().endObject();


        request.mapping(typeName,xContentBuilder);


    }

}
