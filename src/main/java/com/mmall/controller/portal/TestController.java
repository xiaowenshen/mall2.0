package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xiao on 2018/3/4.
 */
@Controller
@RequestMapping("/test/")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("test.do")
    @ResponseBody
    public String testNatApp(String str){
        logger.info("testInfo");
        logger.warn("testwarn");
        logger.error("testerror");
        return "testVal"+ str;
    }

    public static void main(String[] args) {

    }
}
