package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiao on 2018/2/7.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;


    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        //在ShippingMapper中，为insert标签添加 useGeneratedKeys="true" keyProperty="id"
        //id会自动填充到shipping对象的id中
        int count = shippingMapper.insert(shipping);
        if(count > 0){
            Map resultMap = new HashMap();
            resultMap.put("shippiungId",shipping.getId());
            return  ServerResponse.createBySuccess("新建地址成功",resultMap);
        }
        else{
            return ServerResponse.createByErrorMessage("新建地址失败");
        }
    }

    public ServerResponse del(Integer userId, Integer shippingId){
        //注意横向越权问题，下面这种写法会导致横向越权
        //int count = shippingMapper.deleteByPrimaryKey(shippingId);
        int count = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if(count > 0){

            return  ServerResponse.createBySuccess("删除地址成功");
        }
        else{
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
    }

    public ServerResponse update(Integer userId, Shipping shipping) {
        //必须重新赋值userId,否则同样会出现横向越权问题（别人传shipping中的userI东和本用户的userId不一致
        shipping.setUserId(userId);
        int count = shippingMapper.updateByShipping(shipping);
        if(count > 0){

            return  ServerResponse.createBySuccess("修改地址成功");
        }
        else{
            return ServerResponse.createByErrorMessage("修改地址失败");
        }
    }

    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        //必须重新赋值userId,否则同样会出现横向越权问题（别人传shipping中的userI东和本用户的userId不一致
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMessage("无法查询该地址");
        }
        else {
            return ServerResponse.createBySuccess("查找地址成功",shipping);
        }
    }

    public ServerResponse<PageInfo>   getShippingList(Integer userId,int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> list = shippingMapper.selectByUserId(userId);
        PageInfo resultInfo = new PageInfo(list);
        return ServerResponse.createBySuccess(resultInfo);

    }



}
