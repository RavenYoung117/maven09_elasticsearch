package com.hua.entity;

import lombok.Data;

/**
 * maven09_elasticsearch
 * 2019-11-06 16:35
 * Author: JokerHua
 * ******************
 * 【文档注释】:
 */
@Data
public class Book {
    private Integer id;
    private String author;
    private String title;
    private double price;
}
