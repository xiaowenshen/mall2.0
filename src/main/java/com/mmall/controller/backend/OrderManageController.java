package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

/**
 * Created by xiao on 2018/3/8.
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    private static Logger logger = LoggerFactory.getLogger(OrderManageController.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){

            return iOrderService.manageList(pageNum, pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){

            return iOrderService.manageDetail(orderNo); //iOrderService.manageList(pageNum, pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,
                                               @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){


        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){

            return iOrderService.manageSearch(orderNo,pageNum,pageSize); //iOrderService.manageList(pageNum, pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"user do not login,please login as admin");
        }
        //判断管理员
        if(iUserService.checkAdminRole(user).isSuccess()){

            return iOrderService.manageSendGoods(orderNo);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

}
