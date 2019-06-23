package com.lonzh.crawler.controller;

import com.sun.xml.internal.ws.api.pipe.NextAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @ProjectName: Crawler
 * @Package: com.lonzh.crawler.controller
 * @ClassName: Ceshi1111
 * @Description: java类作用描述
 * @Author: qubin
 * @CreateDate: 2019/6/23 22:11
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/6/23 22:11
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class Ceshi1111 {
    public static void main(String[] args) {
        System.out.println("这是第一次修改");
        ArrayList<String> list = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        Iterator<String> it = list.iterator();
        while (it.hasNext()){
            String next = it.next();
            if(next.equals("1")){
                it.remove();
                continue;
            }
        }
    }
}
