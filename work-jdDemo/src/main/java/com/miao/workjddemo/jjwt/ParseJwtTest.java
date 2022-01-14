package com.miao.workjddemo.jjwt;

import io.jsonwebtoken.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseJwtTest {
    public static void main(String[] args) {
        try {
            String compactJws="123";
            Claims claims =
                    Jwts.parser().setSigningKey("xiaocai").parseClaimsJws(compactJws).getBody();

            System.out.println("id:"+claims.getId());
            System.out.println("subject:"+claims.getSubject());
            System.out.println("roles:"+claims.get("roles"));
            System.out.println("logo:"+claims.get("logo"));
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy‐MM‐dd hh:mm:ss");
            System.out.println("签发时间:"+sdf.format(claims.getIssuedAt()));
            System.out.println("过期时间:"+sdf.format(claims.getExpiration()));
            System.out.println("当前时间:"+sdf.format(new Date()) );
        } catch (Exception e) {
            throw new RuntimeException("就是发生异常了，异常原因：" + e.getMessage());
        }
    }
}