package com.miao.workjddemo.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2022-01-12 17:25
 **/
@NoArgsConstructor
@Data
public class SkuMap {

    /**
     * 价格分
     */
    private Integer priceCent;
    /**
     * 价格
     */
    private String price;
    /**
     * 股票
     */
    private Integer stock;
    /**
     * skuid
     */
    private String skuId;
}
