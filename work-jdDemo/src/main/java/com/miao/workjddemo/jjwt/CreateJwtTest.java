package com.miao.workjddemo.jjwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class CreateJwtTest {
      public static void main(String[] args) {
        JwtBuilder builder= Jwts.builder().setId("888") .setSubject("小白")
                  .setIssuedAt(new Date())//设置签发时间
                  .signWith(SignatureAlgorithm.HS256,"xiaocai");//设置签名秘钥
          System.out.println( builder.compact() ); 
            } 
      }