package com.miao.workjddemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: startDemo
 * @description:
 * @author: MiaoWei
 * @create: 2021-11-13 08:11
 **/
@RestController
public class IndexController {

//    @GetMapping({"/","/index"})
//    public String index(){
//        return "index";
//    }

    @GetMapping({"/"})
    public void ip(HttpServletRequest request){
        String ipAddress = IpUtils.getRemoteAddr(request);
        System.out.println("getRemoteAddr = " + ipAddress);
        System.out.println("ipAddress = " + IpUtils.getClientIpAddr(request));
        System.out.println("ipAddress = " + IpUtils.getIpAddr(request));
        System.out.println("ipAddress = " + IpUtils.getClientIpAddress(request));
    }
}
