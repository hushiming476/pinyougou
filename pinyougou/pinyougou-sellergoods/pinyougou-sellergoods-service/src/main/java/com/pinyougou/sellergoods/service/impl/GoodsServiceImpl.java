package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.mapper.service.impl.BaseServiceImpl;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Transactional
@Service
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {
    //商品
    @Autowired
    private GoodsMapper goodsMapper;

    //商品详细信息
    @Autowired
    private GoodsDescMapper goodsDescMapper;

    //商品
    @Autowired
    private ItemMapper itemMapper;

    //商品分类
    @Autowired
    private ItemCatMapper itemCatMapper;

    //品牌
    @Autowired
    private BrandMapper brandMapper;

    //商家
    @Autowired
    private SellerMapper sellerMapper;



    // 1、分页查询
    @Override
    public PageInfo<TbGoods> search(Integer pageNum, Integer pageSize, TbGoods goods) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbGoods.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();
        //不查询已经删除的商品
        criteria.andNotEqualTo("isDelete","1");

        /**
         *  ID
         *  状态：<select v-model="searchEntity.auditStatus">
         *  商品名称：<input v-model="searchEntity.goodsName">
         */
        //模糊查询
        //审核状态
        if (StringUtils.isNotBlank(goods.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        //商家
        if (StringUtils.isNotBlank(goods.getSellerId())) {
            criteria.andEqualTo("sellerId", goods.getSellerId());
        }

        //商品名称模糊查询
        if (StringUtils.isNotBlank(goods.getGoodsName())) {
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    /**
     * 2、新增
     * @param goods 实体
     * @return 操作结果
     */
    @Override
    public void addGoods(Goods goods) {
        //1、保存商品基本信息
        add(goods.getGoods());
        //2、保存商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
        //3、保存商品sku 列表《=====》抽取方法
        saveItemList(goods);
    }

    /**
     * 3、查看商品管理列表
     */
    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();
        //基本
        goods.setGoods(findOne(id));
        //描述
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));

        //sku 列表
        //sql --> select * from tb_item where goods_id=?
        TbItem param = new TbItem();
        param.setGoodsId(id);
        List<TbItem> itemList = itemMapper.select(param);
        goods.setItemList(itemList);
        return goods;
    }

    /**
     * 4、商品修改的业务方法
     */
    @Override
    public void updateGoods(Goods goods) {
        //1、更新商品基本信息
        goods.getGoods().setAuditStatus("0");
        update(goods.getGoods());
        //2、更新商品描述信息
        goodsDescMapper.selectByPrimaryKey(goods.getGoodsDesc());
        //3、根据商品spu id 删除sku
        //sql --> delete from tb_item where goods_id=?
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);
        //4、保存商品sku
        saveItemList(goods);

    }

    /**
     *5、 根据商品spu id数组更新商品spu 的审核状态
     * @param status 商品spu状态
     * @param ids 商品spu id数组
     * @return 操作结果
     */
    @Override
    public void updateStatus(String status, Long ids) {
        /**
         * -- 根据商品spu id数组更新商品spu 的审核状态为1
         * update tb_goods set aduit_status=? where id in(?,?...)
         */
        //要更新的数据
        TbGoods tbGoods = new TbGoods();
        tbGoods.setAuditStatus(status);
        //更新的条件
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));

        goodsMapper.updateByExampleSelective(tbGoods,example);

        //实现运营商--查看商品审核列表
        TbItem tbItem = new TbItem();
        tbItem.setStatus("0");
        //审核通过
        if ("2".equals(status)){
            tbItem.setStatus("1");
        }else {
        //审核不通过
            tbItem.setStatus("0");
        }
        // 更新的条件
        Example itemExample = new Example(TbItem.class);
        itemExample.createCriteria().andIn("goodsId",Arrays.asList(ids));
        itemMapper.updateByExampleSelective(tbItem,itemExample);
    }

    /**
     * 5.1运营商--商品审核：删除，逻辑删除-update
     */
    @Override
    public void deleteGoodsByIds(Long[] ids) {
        //更新商品spu 的删除状态修改为1
        //sql --> update tb_goods set is_delete='1' where id in(?,?,..)
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete("1");


        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);

    }

    
    /**
     * 6、保存商品sku
     * @param goods 商品vo
     */
    
    public void saveItemList(Goods goods){
        if ("1".equals(goods.getGoods().getIsEnableSpec())){
            //1、启用规格
            if(goods.getItemList() !=null && goods.getItemList().size()>0){
                for (TbItem tbItem :goods.getItemList() ) {
                    String title = goods.getGoods().getGoodsName();
                //sku标题 ：spu的商品名称 + 所有规格的选项值（spec中的规格对应的值)
                    if (StringUtils.isNotBlank(tbItem.getSpec())){
                        //转换成json字符串
                        Map<String,String> specMap = JSON.parseObject(tbItem.getSpec(),Map.class);
                       for (Map.Entry<String,String>entry:specMap.entrySet()){
                           title = " " + entry.getValue();
                       }
                    }
                    tbItem.setTitle(title);
                    setItemValue(tbItem,goods);
                    //保存商品sku
                    itemMapper.insertSelective(tbItem);
                }
            }

        }else {
            //如果不启用规格：创建一个sku
                TbItem tbItem = new TbItem();
                tbItem.setTitle(goods.getGoods().getGoodsName());
            //spec:来自spu
                tbItem.setSpec("{}");
            //价格-price
                tbItem.setPrice(goods.getGoods().getPrice());
            //状态-status:0
                tbItem.setStatus("0");
            //是否默认-isDefault:1
                tbItem.setIsDefault("1");
            //库存：9999
                tbItem.setNum(9999);
                setItemValue(tbItem,goods);
                itemMapper.insertSelective(tbItem);

        }
        
    }

            /**
            *7、 设置sku数据
            * @param tbItem sku商品
            * @param goods 商品vo
            */
            private void setItemValue(TbItem tbItem, Goods goods) {
                //图片 ：获取spu图片列表中的第一张图片
                    if (StringUtils.isNotBlank(goods.getGoodsDesc().getItemImages())){
                    List<Map>imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
                    for (Map map : imageList) {
           // [
          // {"color":"黑色","url":"http://img11.360buyimg.com/n1/s450x450_jfs/t3076/42/8593902551/206108/fdb1a60f/58c60fc3Nf9faa2fa.jpg"},
          // {"color":"金色","url":"http://img11.360buyimg.com/n1/s450x450_jfs/t3076/42/8593902551/206108/fdb1a60f/58c60fc3Nf9faa2fa.jpg"}
         // ]
                    tbItem.setImage(imageList.get(0).get("url").toString());
                //商品分类id：来自spu的第3级商品分类id
                    tbItem.setCategoryid(goods.getGoods().getCategory3Id());
                //商品分类中文名称：根据spu的第3级商品分类id查找商品分类中文名称
                    TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbItem.getCategoryid());
                    tbItem.setCategory(tbItemCat.getName());
                //品牌中文名称：来spu的品牌id查询品牌
                    TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
                //商家id：来自spu的商家id
                    tbItem.setSellerId(goods.getGoods().getSellerId());
                //商家中文名称： 根据spu的商家id查询商家中文名称
                    TbSeller seller = sellerMapper.selectByPrimaryKey(tbItem.getSellerId());
                    tbItem.setSeller(seller.getName());
                //其它属性从前台携带过来，要么复制自spu。
                    tbItem.setGoodsId(goods.getGoods().getId());
                    tbItem.setCreateTime(new Date());
                    tbItem.setUpdateTime(tbItem.getCreateTime());
            }
        }
    }




}
