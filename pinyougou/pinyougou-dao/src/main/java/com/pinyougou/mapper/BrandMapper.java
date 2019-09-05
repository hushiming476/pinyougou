package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;


import java.util.List;
import java.util.Map;

public interface BrandMapper extends BaseMapper<TbBrand> {
    // 查询全部品牌
    List<TbBrand> queryAll();
    // 分类模板
    List<Map<String,String>> selectOptionList();
}
