package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.service.impl.BaseServiceImpl;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationOptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpecificationOptionServiceImpl extends BaseServiceImpl<TbSpecificationOption> implements SpecificationOptionService {

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageInfo<TbSpecificationOption> search(Integer pageNum, Integer pageSize, TbSpecificationOption specificationOption) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbSpecificationOption.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(specification.getProperty())) {
         criteria.andLike("property", "%" + specification.getProperty() + "%");
         }*/
        List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);
        return new PageInfo<>(list);
    }


}
