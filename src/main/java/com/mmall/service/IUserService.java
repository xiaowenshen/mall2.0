package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.omg.CORBA.Object;

/**
 * Created by 1032019725 on 2017/8/16.
 */
public interface IUserService {
    /**
     * 登陆接口
     * @param username
     * @param userpwd
     * @return
     */
    ServerResponse<User> login(String username, String userpwd);

    /**
     * 注册接口
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 验证是否在数据库中存在type类型的，值为str的数据
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str,String type);

    /**
     * 找回密码问题接口
     * @param username
     * @return
     */
    ServerResponse selectQuestion(String username);

    /**
     * 校验问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username,String question,String answer);

    /**
     * 使用token重置密码
     * @param username
     * @param userpassword
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetRestPassword(String username,String userpassword,String forgetToken );

    /**
     * 登陆状态下修改密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    ServerResponse<String> rsetPassword(String passwordOld,String passwordNew,User user);

    /**
     * 更新个人信息
     * @param user
     * @return
     */
    ServerResponse<User> updateInfomation(User user);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    ServerResponse<User> getInformation(Integer userId);

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    ServerResponse checkAdminRole(User user);
}
