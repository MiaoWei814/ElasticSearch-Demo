package com.miao.apidemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2021-11-12 20:48
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private Integer age;
}
