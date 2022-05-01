package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //1 查询所有
    @ApiOperation(value="获取所有医院设置信息")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){

        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //2 逻辑删除
    @ApiOperation(value="逻辑删除信息")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean rows = hospitalSetService.removeById(id);
        if(rows){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    //3 多条件查询和分页查询
    @PostMapping("findPage/{current}/{limit}")
    public Result pageFindAll(@PathVariable long current,
                              @PathVariable long limit,
                              @RequestBody (required = false) HospitalSetQueryVo hospitalSetQueryVo){//(required = false)默认可以为null
        Page<HospitalSet> page=new Page<>(current,limit);
        QueryWrapper<HospitalSet> wrapper=new QueryWrapper<>();
        String hostname=hospitalSetQueryVo.getHosname();
        String hostcode=hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty(hostname)){
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hostcode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        Page<HospitalSet> pageList = hospitalSetService.page(page, wrapper);
        return Result.ok(pageList);
    }

    //4 添加医院设置
    @PostMapping("setHospitalSet")
    public Result insertHosp(@RequestBody HospitalSet hospitalSet){
        //设置签名密钥随机数+MD5加密
        Random random=new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+random.nextInt(1000)+""));
        //设置状态1表示可用0表示不可用
        hospitalSet.setStatus(1);
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //5根据id查询医院设置信息
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        try{
            int a=1/0;
        }catch (Exception e){
            throw new YyghException("代码错误",201);
        }

        return Result.ok(hospitalSet);
    }

    //6 修改医院设置信息
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);

        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //7 根据id批量删除医院设置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospSet(@RequestBody List<Integer> idlist){
        boolean flag = hospitalSetService.removeByIds(idlist);
        if(flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    //8 医院设置锁定和解锁
    @PutMapping("lockHospitaiSet/{id}/{staus}")
    public Result lockHospitaiSet(@PathVariable Long id,
                       @PathVariable Integer staus){
        //根据id获取医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置status
        hospitalSet.setStatus(staus);
        //调用
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    //9 发送签名密钥
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.getHoscode();
        hospitalSet.getSignKey();
        //TODO
        return Result.ok();

    }
}
