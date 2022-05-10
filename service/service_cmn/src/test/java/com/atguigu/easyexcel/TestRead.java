package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        String filename="C:\\Users\\lzm\\Desktop\\1.csv";

        EasyExcel.read(filename,UserData.class,new ExcelListener()).sheet().doRead();
    }


}
