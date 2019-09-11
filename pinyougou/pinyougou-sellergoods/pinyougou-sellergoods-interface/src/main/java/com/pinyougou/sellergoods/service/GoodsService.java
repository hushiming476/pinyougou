package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;


public interface GoodsService extends BaseService<TbGoods> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param goods 搜索条件
     * @return 分页信息
     */
    PageInfo<TbGoods> search(Integer pageNum, Integer pageSize, TbGoods goods);

    /**
     * 新增
     * @param goods 实体
     * @return 操作结果
     */
    void addGoods(Goods goods);

    /**
     * 根据商品spu id查询商品vo
     * @param id 商品spuID
     * @return vo
     */
    Goods findGoodsById(Long id);

    /**
     * 修改
     * @param goods 实体
     * @return 操作结果
     */
    void updateGoods(Goods goods);

    /**
     * 根据商品spu id数组更新商品spu 的审核状态
     * @param status 商品spu状态
     * @param ids 商品spu id数组
     * @return 操作结果
     */
    void updateStatus(String status, Long ids);

    /**
     * 运营商--商品审核：删除，逻辑删除-update
     */
    void deleteGoodsByIds(Long[] ids);


}
