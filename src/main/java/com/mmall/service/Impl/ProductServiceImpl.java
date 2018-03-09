package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1032019725 on 2017/9/7.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;
    /**
     * 对产品进行更新或者修改操作
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product)
    {
        if(product != null){
            //判断子图是否为空，不为空就把子图的第一个赋值给主图
            if(org.apache.commons.lang3.StringUtils.isNoneBlank(product.getSubImages())){

                String[] subImageArry = product.getSubImages().split(",");
                if(subImageArry.length>0){
                    product.setMainImage(subImageArry[0]);
                }
            }
            //根据id更新或者插入
            if(product.getId() != null){
                //更新
                int rowcount = productMapper.updateByPrimaryKey(product);
                if(rowcount > 0){
                 return    ServerResponse.createBySuccess("更新产品信息成功");
                }
                else {
                    return ServerResponse.createByErrorMessage("更新失败");
                }
            }
            else {
                //插入
                int rowcount = productMapper.insert(product);
                 if(rowcount > 0){
                    return    ServerResponse.createBySuccess("添加产品信息成功");
                }
                else {
                    return ServerResponse.createByErrorMessage("添加失败");
                }
            }
        }
        return ServerResponse.createByErrorMessage("更新或添加失败");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int  rowCount=productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return    ServerResponse.createBySuccess("修改产品销售信息成功");
        }
        else {
            return ServerResponse.createByErrorMessage("修改产品销售信息失败");
        }

    }

    /**
     * 查找商品详情
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已经下架或者删除");
        }else {
            //业务逻辑简单，直接返回VO对象  value object
            //后面可演变成pojo-->bo(business object)-->vo(view object)
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);

        }
    }

    /**
     * 拼装productdetailvo
     * @param product
     * @return
     */
   private  ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());


        //ImageHost
       // 需要从配置文件中读取，便于代码与配置隔离，配置以后会优化成热部署配置，还有建立配置中心去管理配置，并且可以热部署
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //parentCategoryId
       Category category =categoryMapper.selectByPrimaryKey(product.getId());
       if(category!=null){
           productDetailVo.setParentCategoryId(category.getParentId());
       }else {
           productDetailVo.setParentCategoryId(0);
       }

        //createtime,
       //time是毫秒数，需要转化，Dateutil 使用joda-time包
       productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));

        //updatetime
       productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return  productDetailVo;
   }

    /**
     * 获取所有的产品的分页列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo>   getProductList(int pageNum,int pageSize){
        //先启动配置
        //填充自己的sql查询逻辑
        //使用pagehelper
        PageHelper.startPage( pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        //我们不需要product里面的所有信息，所以我们创建一个productVO，用于响应前端
        //填充好自己的sql逻辑
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        //利用product进行分页
        PageInfo pageResult = new PageInfo(productList);
        //把显示的list进行替换
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 根据商品名、商品ID查询商品列表
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(productName)){

            //把name字符串拼装成%name%用于模糊查询
             productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        PageInfo pageResult = new PageInfo(productList);
        List<ProductListVo> productListVoList = Lists.newArrayList();

        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 拼装product
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        if(product == null)
        {
            return null;
        }
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setPrice(product.getPrice());
        product.setStatus(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }


    /**
     * 根据商品id获取详情
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已经下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已经下架或者删除");
        }

            //业务逻辑简单，直接返回VO对象  value object
            //后面可演变成pojo-->bo(business object)-->vo(view object)
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);
    }


    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword)&& categoryId == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //把当前节点的所有子节点和本身的categoryid放到这个集合，便于查询
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category ==null&&StringUtils.isBlank(keyword)){
                //无分类，且关键字为空
                //返回空结果集
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank( keyword)?null:keyword,categoryIdList.size() == 0?null:categoryIdList);
        PageHelper.startPage(pageNum,pageSize);
        PageInfo resultInfo = new PageInfo(productList);
        List<ProductListVo>  productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        resultInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(resultInfo);
    }


}

