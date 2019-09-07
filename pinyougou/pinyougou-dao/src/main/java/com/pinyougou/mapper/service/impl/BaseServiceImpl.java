package com.pinyougou.mapper.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BaseMapper;
import com.pinyougou.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

    //spring 4.x+
    // 泛型依赖注入；根据泛型类型查找ioc容器中的对应mapper 如TbBrand  brandMapper
    @Autowired
    private BaseMapper<T> baseMapper;

    /*public void setBaseMapper(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }*/

    @Override
    public T findOne(Serializable id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> findAll() {
        return baseMapper.selectAll();
    }

    @Override
    public List<T> findByWhere(T t) {
        return baseMapper.select(t);
    }

    @Override
    public PageInfo<T> findPage(Integer pageNum, Integer pageSize) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);

        List<T> list = baseMapper.selectAll();
        return new PageInfo<T>(list);
    }

    @Override
    public PageInfo<T> findPage(Integer pageNum, Integer pageSize, T t) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);

        List<T> list = baseMapper.select(t);
        return new PageInfo<T>(list);
    }

    @Override
    public void add(T t) {
        baseMapper.insertSelective(t);
    }

    @Override
    public void update(T t) {
        baseMapper.updateByPrimaryKeySelective(t);
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        if (ids != null && ids.length > 0) {
            baseMapper.deleteByIds(StringUtils.join(ids, ","));
        }
    }
}
