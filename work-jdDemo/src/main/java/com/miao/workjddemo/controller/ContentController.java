package com.miao.workjddemo.controller;

import com.miao.workjddemo.pojo.ParseSource;
import com.miao.workjddemo.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2021-11-15 22:12
 **/
@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;


    /**
     * 插入
     *
     * @param keyword 关键字
     * @return {@link Boolean}
     * @throws IOException ioexception
     */
    @GetMapping("/parse/{keyword}")
    public Boolean insert(@PathVariable String keyword) throws IOException {
        return contentService.insert(keyword);
    }

    /**
     * 搜索
     *
     * @param keyword   关键字
     * @param orderNum  订单num
     * @param orderSize 订单的大小
     * @return {@link List}<{@link String}>
     * @throws IOException ioexception
     */
    @GetMapping("/search/{keyword}/{orderNum}/{orderSize}")
    public List<ParseSource> search(@PathVariable String keyword, @PathVariable Integer orderNum, @PathVariable Integer orderSize) throws IOException {
        return contentService.search(orderNum, orderSize, keyword);
    }
}
