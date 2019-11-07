package com.hua.service;

import java.util.Map;

/**
 * maven09_elasticsearch
 * 2019-11-06 14:56
 * Author: JokerHua
 * ******************
 * 【文档注释】:
 */

public interface SearchService {
    void createIndex(String indexName) throws Exception;

    void deleteIndex(String indexName) throws Exception;

    void addData(String indexName) throws Exception;

    boolean existIndex(String indexName) throws Exception;

    void  saveOrUpdate(String json,int id) throws  Exception;

    void   deleteById(String id) throws  Exception;

    void queryAll() throws Exception;

    void   matchQuery(String keyword) throws  Exception;


    void   rangeQuery() throws  Exception;

    void   fuzzyQuery(String  keyword) throws  Exception;


    void  sortById() throws  Exception;

    void   queryByPage(int startIndex,int pageSize)throws  Exception;

    //组合查询
    //条件+范围+排序+分页+聚合+高亮
    void query(Map<String,Object> map) throws Exception;
}
