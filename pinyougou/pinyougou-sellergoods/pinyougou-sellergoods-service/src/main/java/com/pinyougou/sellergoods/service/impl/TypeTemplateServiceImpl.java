package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.mapper.service.impl.BaseServiceImpl;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl extends BaseServiceImpl<TbTypeTemplate> implements TypeTemplateService {

    // 注入分类模板
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    // 注入品牌接口
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageInfo<TbTypeTemplate> search(Integer pageNum, Integer pageSize, TbTypeTemplate typeTemplate) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbTypeTemplate.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        if (StringUtils.isNotBlank(typeTemplate.getName())) {
            criteria.andLike("name", "%" + typeTemplate.getName() + "%");
        }

        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        return new PageInfo<>(list);
    }



}
