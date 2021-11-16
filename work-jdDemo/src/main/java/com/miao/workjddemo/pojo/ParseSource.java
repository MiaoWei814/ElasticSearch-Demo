package com.miao.workjddemo.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2021-11-15 21:56
 **/
@Data
@Accessors(chain = true)
public class ParseSource {
    private String parseImg;
    private String parseContext;
    private String parsePrice;
    private String parseShopName;

}
