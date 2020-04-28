package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/***
  * 适配旧版本分页
 *
  * @author yangch
  * @time 2018.06.22 18:52
  */
@Data
public class PageData<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> content;
    /**
     * 总记录数
     */
    private long totalElements;
    /**
     * 总分页数
     */
    private long totalPages;
    /**
     * 分页开始页面
     */
    private long number;
    /**
     * 每页数量
     */
    private long size;

    public PageData(IPage<T> pageInfo, List<T> content) {
        this.totalElements = pageInfo.getTotal();
        this.totalPages = pageInfo.getPages();
        //兼容性问题，为getPageNum的前一页
        this.number = pageInfo.getCurrent();
        this.size = pageInfo.getSize();
        this.content = content;
    }

    public PageData(IPage<T> pageInfo) {
        this(pageInfo, pageInfo.getRecords());
    }

}
