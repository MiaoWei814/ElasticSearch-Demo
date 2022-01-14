package com.miao.workjddemo.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2022-01-12 17:03
 **/
@NoArgsConstructor
@Data
public class SkuDetail {

    /**
     * 名字
     */
    private String names;
    /**
     * 编号
     */
    private String pvs;
    /**
     * sku的id
     */
    private String skuId;

    /**
     * 价格分
     */
    private Integer priceCent;
    /**
     * 价格
     */
    private String price;
    private Integer stock;

    /**
     * 图片地址
     */
    private String imgUrl;
}
