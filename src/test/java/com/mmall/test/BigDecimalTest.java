package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by xiao on 2018/1/26.
 */
public class BigDecimalTest {

    @Test
    public void  test1(){
        System.out.println( 0.05+0.01);
        System.out.println( 1.0-0.42);
        System.out.println( 4.015*100);
        System.out.println( 123.1/100);
    }

    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.04");
        System.out.println(b1.add(b2));
    }
}
