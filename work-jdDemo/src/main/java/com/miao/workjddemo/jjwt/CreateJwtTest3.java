package com.miao.workjddemo.jjwt;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class CreateJwtTest3 {
    public static void main(String[] args) {
//为了方便测试，我们将过期时间设置为1分钟
        long now = System.currentTimeMillis();//当前时间
        long exp = now + 1000 * 60;//过期时间为1分钟
        JwtBuilder builder = Jwts.builder().setId("888")
                .setSubject("小白")
                .setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "xiaocai").setExpiration(DateUtil.offsetDay(new Date(), 1))
                .claim("roles", "admin").claim("logo", "logo.png");
        System.out.println(builder.compact());
    }
}