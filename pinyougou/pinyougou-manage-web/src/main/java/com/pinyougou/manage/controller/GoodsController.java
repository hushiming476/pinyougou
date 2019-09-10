package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/goods")
@RestController
public class GoodsController {

   // @Reference(retries = 0, timeout = 10000)
    @Reference(retries = 0,timeout = 10000)
    private GoodsService goodsService;

    /**
     *1、 新增
     * @param goods 实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods){
        try {
            // 设置商家权限
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            // 未审核
            goods.getGoods().setAuditStatus("0");
            goodsService.addGoods(goods);

            return Result.ok("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增失败");
    }

    /**
     * 2、根据主键查询
     * @param id 主键
     * @return 实体
     */
    @GetMapping("/findOne/{id}")
    public Goods findOne(@PathVariable Long id){
        return goodsService.findGoodsById(id);
    }

    /**
     * 3、修改
     * @param goods 实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods){

        try {
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (sellerId.equals(oldGoods.getSellerId())){
            goodsService.updateGoods(goods);
            }else {
                return Result.fail("非法修改");
            }
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 4、根据主键数组批量删除
     * @param ids 主键数组
     * @return 实体
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            goodsService.deleteGoodsByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     *5、 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param goods 搜索条件
     * @return 分页信息
     */
    @PostMapping("/search")
    public PageInfo<TbGoods> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                           @RequestBody TbGoods goods) {
        return goodsService.search(pageNum, pageSize, goods);
    }

    /**
     *6、根据商品spu id数组更新商品spu 的审核状态
     * @param status 商品spu状态
     * @param ids 商品spu id数组
     * @return 操作结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(String status,Long ids){
        try {
            goodsService.updateStatus(status,ids);
            return Result.ok("更新商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新商品状态失败！");
    }


}
