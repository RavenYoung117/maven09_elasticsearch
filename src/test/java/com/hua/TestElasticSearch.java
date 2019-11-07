package com.hua;

import com.alibaba.fastjson.JSON;
import com.hua.entity.Book;
import com.hua.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * maven09_elasticsearch
 * 2019-11-06 14:51
 * Author: JokerHua
 * ******************
 * 【文档注释】:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticSearchApplication.class)
public class TestElasticSearch {
    @Resource
    private SearchService searchService;
    @Test
    public void test1() throws  Exception{
        //indexName必须全小写
        //searchService.createIndex("bookstore");
        Book book = new Book();
        book.setAuthor("小王八蛋");
        book.setTitle("html5从入门到放弃");
        book.setPrice(99);

        String json= JSON.toJSONString(book);
        searchService.addData(json);
    }
    @Test
    public void saveOrUpdate() throws  Exception{
        for (int i = 1; i <= 10; i++) {
            Book book = new Book();
            book.setAuthor("宇宙无敌丑");
            book.setTitle("mysql从入门到放弃");
            book.setPrice(99);
            book.setId(i);
            String json= JSON.toJSONString(book);
            searchService.saveOrUpdate(json,book.getId());
        }
    }

    @Test
    public void saveOrUpdate2() throws  Exception{
        for (int i = 5; i <= 10; i++) {
            Book book = new Book();
            book.setAuthor("猪见了都吐");
            book.setTitle("PHP从入门到砸电脑");
            book.setPrice(999);
            book.setId(i);
            String json= JSON.toJSONString(book);
            searchService.saveOrUpdate(json,book.getId());
        }
    }
    @Test
    public void deleteById() throws  Exception{
            searchService.deleteById("10");
    }
    @Test
    public void queryAll() throws  Exception{
        searchService.queryAll();
    }
    @Test
    public void matchQuery() throws  Exception{
        searchService.matchQuery("砸电脑");
    }
    @Test
    public void rangeQuery() throws  Exception{
        searchService.rangeQuery();
    }
    @Test
    public void fuzzyQuery() throws  Exception{
        searchService.fuzzyQuery("mysql");
        System.out.println("--------");
        searchService.sortById();
    }
    @Test
    public void queryByPage() throws  Exception{
        searchService.queryByPage(0,3);
        searchService.queryByPage(3,3);
        searchService.queryByPage(6,3);
    }
    @Test
    public void query() throws  Exception{
        Map<String,Object> map = new HashMap();
        map.put("startIndex",0);
        map.put("pageSize",3);
        map.put("keyword","砸电脑");
        searchService.query(map);
    }

}

