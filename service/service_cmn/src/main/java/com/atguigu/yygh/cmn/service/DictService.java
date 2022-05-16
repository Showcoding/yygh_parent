package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {

    //根据id查询子节点
    List<Dict> findChildData(Long id);

    //导出表格
    void exportDictData(HttpServletResponse response);

    //导入表格
    void importDictData(MultipartFile file);

    //根据dictCode和value查询 因为value可能重复 所以需要根据两个值查询
    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
