package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
@RestController //将本类中的所有方法的返回结果都当做输出内容输出到浏览器
public class BrandController {

    @Reference
    private BrandService brandService;
    @Reference
    private TypeTemplateService typeTemplateService;
    @Reference
    private SpecificationService specificationService;

    /**
     9、查询所有品牌列表；格式为：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     @return 品牌列表；格式为：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,String>>selectOptionList(){
        return brandService.selectOptionList();
    }


    /**
     * 8、根据主键查询
     *
     */
    @GetMapping("/findOne/{id}")
    public TbBrand findOne(@PathVariable Long id){
       return brandService.findOne(id);

    }

    /**
     * 7、根据分页信息和查询条件分页模糊查询品牌数据
     * @param pageNum 页号
     * @param pageSize 页大小
     * @return 分页信息对象
     */
    @PostMapping ("/search")
    public PageInfo<TbBrand> search(@RequestParam(defaultValue = "1")Integer pageNum,
                                      @RequestParam(defaultValue = "10")Integer pageSize,
                                    @RequestBody TbBrand brand){

        return brandService.search(pageNum, pageSize,brand);
    }

    /**
     *6、 删除品牌数据
     *
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除品牌数据成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除品牌数据失败");

    }

    /**
     *5、 更新品牌数据
     *
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return Result.ok("更新品牌数据成功 ^_^");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新品牌数据失败 ^~^ ");

    }


    /**
     *4、 新增品牌数据
     *
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return Result.ok("恭喜您，新增品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增品牌失败了，请不要瞎搞");

    }

    /**
     * 3、分页查询品牌数据
     * @param pageNum 页号
     * @param pageSize 页大小
     * @return 分页信息对象
     */
    @GetMapping("/findPage")
    public PageInfo<TbBrand> findPage(@RequestParam(defaultValue = "1")Integer pageNum,
                                      @RequestParam(defaultValue = "10")Integer pageSize){

        return brandService.findPage(pageNum, pageSize);
    }

    /**
     * 2、分页查询品牌数据
     * @param pageNum 页号
     * @param pageSize 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(defaultValue = "1")Integer pageNum,
                                  @RequestParam(defaultValue = "10")Integer pageSize){

        return brandService.findPage(pageNum, pageSize).getList();
    }

    /**
     * 1、查询全部品牌
     */
    @GetMapping("/findAll")
        public List<TbBrand> findAll(){
        return brandService.findAll();
    }
}
