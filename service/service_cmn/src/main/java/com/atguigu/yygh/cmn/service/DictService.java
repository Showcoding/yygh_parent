package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.cmn.service.impl.DictServiceImpl;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DictService extends IService<Dict> {

    List<Dict> findChildData(Long id);
}
