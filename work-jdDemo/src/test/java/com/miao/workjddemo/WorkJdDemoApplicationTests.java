package com.miao.workjddemo;

import cn.hutool.core.text.CharSequenceUtil;
import com.miao.workjddemo.util.HtmlParseUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WorkJdDemoApplicationTests {

    @Autowired
    private HtmlParseUtil htmlParseUtil;
    @Test
    void contextLoads() {
        htmlParseUtil.parseJDBook("java");

    }

}
