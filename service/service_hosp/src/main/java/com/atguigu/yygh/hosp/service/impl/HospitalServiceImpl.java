package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.cmn.client.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import netscape.javascript.JSObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> paramMap) {
        //将map集合转化成字符串 再转换为json对象
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        //判断是否有数据
        String hoscode = hospital.getHoscode();
//        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);
        Hospital hospitalExist=hospitalRepository.getHospitalByHoscode(hoscode);

        //如果存在 则是修改操作
        if(hospitalExist!=null){
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else { //如果不存在 则是添加操作
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> selectHospPage(int page, int limit, HospitalQueryVo hospitalQueryVo) {
        Pageable pageable= PageRequest.of(page-1,limit);

        //构造match条件
        ExampleMatcher matcher=ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //把HospitalQueryVo转成HospitalQuery
        Hospital hospital=new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        Example<Hospital> example=Example.of(hospital,matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        //因为Hospital没有HospType 但是Hospital继承了BaseMongoEntity 里面有Map类型的param 可以把数据放到里面然后获取
        pages.getContent().stream().forEach(item->{
            this.setHospitaiHosType(item);
        });
        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询
        Hospital hospital = hospitalRepository.findById(id).get();

        //设置更新状态和更新时间
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String, Object> result = new HashMap<>();

        Hospital hospital = this.setHospitaiHosType(hospitalRepository.findById(id).get());
        result.put("hospital", hospital);

        //单独处理更直观
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;

    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital!=null){
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    //根据医院编号获取预约挂号详情
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String,Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitaiHosType(this.getByHoscode(hoscode));
        result.put("hospital",hospital);
        //预约规则
        result.put("bookingRule",hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }

    private Hospital setHospitaiHosType(Hospital hospital) {
        //调用cmn dictfrignclient方法 获取医院等级
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());


        //调用cmn dictfeignclient方法 获取医院省 市 区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospital.getParam().put("hostypeString",hostypeString);
        hospital.getParam().put("fullAdress",provinceString+cityString+districtString);
        return hospital;
    }

}
