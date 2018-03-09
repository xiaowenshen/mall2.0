package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1032019725 on 2017/9/7.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;
    /**
     * 添加或修改商品信息
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //TODO 填充添加商品业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    /**
     * 修改商品销售状态信息
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //TODO 填充添加商品业务逻辑
            return iProductService.setSaleStatus(productId,status);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     *
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session,Integer productId)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //TODO 填充添加商品业务逻辑
                return iProductService.manageProductDetail(productId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value ="pageNum",defaultValue = "1")int pageNum,@RequestParam(value = "pageSzie",defaultValue = "10")int pageSize)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //TODO 填充添加商品业务逻辑
            return iProductService.getProductList(pageNum,pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, @RequestParam(value ="pageNum",defaultValue = "1")int pageNum,@RequestParam(value = "pageSzie",defaultValue = "10")int pageSize,Integer productId,String productName)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充添加商品业务逻辑
            return iProductService.searchProduct(productName, productId,pageNum,pageSize);
        }
        else {
        return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //通过httpservletRequest 来获取相对路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            //和前端约定好要把URL拼出来
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);

        }
        else {
            return ServerResponse.createByErrorMessage("no right");
        }
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richImgUpload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request,HttpServletResponse response){

        Map resultMap = Maps.newHashMap();
        //富文本中对于返回值有自己的要求，我们用的是simditor，所以我们要按照simditor
        //官方文档http://simditor.tower.im/docs/doc-config.html
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //通过httpservletRequest 来获取相对路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            //上传文件
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            //和前端约定好要把URL拼出来
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            //上传成功后，需要对返回的response header进行处理
            response.addHeader("Access-Conrol-Allow-Headers","X-File-Name");

            return resultMap;

        }
        else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");

            return resultMap;
        }
    }

}
