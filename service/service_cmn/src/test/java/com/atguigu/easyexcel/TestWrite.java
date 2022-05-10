package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {

        String fileName="C:\\Users\\lzm\\Desktop\\1.csv";
        List<UserData> list=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserData userData=new UserData();
            userData.setUid(i);
            userData.setUsername("lzm"+i);
            list.add(userData);
        }
        EasyExcel.write(fileName,UserData.class).sheet("用户信息")
                .doWrite(list);
    }
}
