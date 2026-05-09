package com.example.unit.dto;

import java.util.List;

/**
 * 分页结果封装类
 * 
 * @param <T> 数据类型
 * @author System
 * @version 1.0.0
 */
public class PageResult<T> {

    private List<T> records;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer pages;

    public PageResult() {}

    public PageResult(List<T> records, Long total, Integer page, Integer size, Integer pages) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = pages;
    }

    // Getters and Setters
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public static <T> PageResult<T> of(List<T> records, Long total, Integer page, Integer size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setPages((int) Math.ceil((double) total / size));
        return result;
    }
}
