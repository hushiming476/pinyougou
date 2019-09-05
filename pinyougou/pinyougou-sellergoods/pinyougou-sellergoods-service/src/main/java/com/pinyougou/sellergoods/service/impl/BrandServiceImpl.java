package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.mapper.service.impl.BaseServiceImpl;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;


    @Override
    public List<TbBrand> queryAll() {
        return brandMapper.queryAll();
    }

    @Override
    public List<TbBrand> testPage(Integer pageNum, Integer pageSize) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);

        return brandMapper.selectAll();
    }


    // 业务需求：根据品牌名称、首字母模糊条件查询并根据分页信息进行分页
    @Override
    public PageInfo<TbBrand> search(Integer pageNum, Integer pageSize, TbBrand brand) {
        // 开启分页查询
        PageHelper.startPage(pageNum,pageSize);
        // 创建分页查询条件
        Example example = new Example(TbBrand.class);
        // 查询条件对象
        Example.Criteria criteria =example.createCriteria();
       // 根据首字母
        if (StringUtils.isNotBlank(brand.getFirstChar())){
            criteria.andEqualTo("firstChar",brand.getFirstChar());
        }
        // 根据品牌名称模糊查询
        if (StringUtils.isNotBlank(brand.getName())){
            criteria.andLike("name","%"+brand.getName()+"%");
        }
        List<TbBrand> list = brandMapper.selectByExample(example);
        // 返回分页查询结果
       //return PageInfo<list>;
        return PageInfo.of(list);
    }

    /**
     查询所有品牌列表；格式为：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     @return 品牌列表；格式为：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     */
    @Override
    public List<Map<String, String>> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
