package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {


    /**
     *1\ 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param typeTemplate 搜索条件
     * @return 分页信息
     */
    PageInfo<TbTypeTemplate> search(Integer pageNum, Integer pageSize, TbTypeTemplate typeTemplate);


}
