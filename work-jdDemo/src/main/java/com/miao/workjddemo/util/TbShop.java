package com.miao.workjddemo.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2022-01-11 14:40
 **/
@Getter
@Setter
public class TbShop {
    //商品id
    private String goodsId;
    //商品主图
    private String bigImg;
    //商品小图
    private List<String> smallImg;
    //描述
    private String describe;
    //价格
    private String price;
    //总销量
    private String sale;
    //评价
    private String evaluate;

    @Override
    public String toString() {
        return "商品id："+goodsId+" 主图地址："+bigImg+""+"小图地址："+smallImg.toString()+"描述："+describe+" 价格："+price+" 总销量："+sale+" 评价:"+evaluate;
    }
}
