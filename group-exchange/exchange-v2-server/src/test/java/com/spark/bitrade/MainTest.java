package com.spark.bitrade;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

/**
 *  
 *
 * @author young
 * @time 2019.09.02 14:41
 */
// 获取启动类，加载配置，确定装载 Spring 程序的装载方法，它回去寻找 主配置启动类（被 @SpringBootApplication 注解的）
//@SpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MainTest {
    @org.junit.Test
    public void test() {

        System.out.println("test---------" + hashCode());
    }

    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
//        Set<String> duplicate = new HashSet<>();
//        for (int i = 0; i < 10000; i++) {
//            duplicate.add("a" + i);
//        }
//        System.out.println(duplicate.add("aaa"));
//        System.out.println(duplicate.add("aaa"));
//        System.out.println("time=" + (System.currentTimeMillis() - startTime));

        System.out.println(System.currentTimeMillis());
    }
}
