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
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

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




    @Override
    public PageInfo<TbGoods> search(Integer pageNum, Integer pageSize, TbGoods goods) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbGoods.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(goods.getProperty())) {
            criteria.andLike("property", "%" + goods.getProperty() + "%");
        }*/

        List<TbGoods> list = goodsMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    /**
     * 新增
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
     * 保存商品sku
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
            * 设置sku数据
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
