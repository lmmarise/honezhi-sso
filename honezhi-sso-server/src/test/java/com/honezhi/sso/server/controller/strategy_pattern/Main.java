package com.honezhi.sso.server.controller.strategy_pattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Constructor;

/**
 * @author： tsb
 * @date： 2020/8/17
 * @description：
 * @modifiedBy：
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Main {

    @Autowired
    private CacheConfig cacheConfig;

    @Test
    public void main() {

        Context context = new Context(new OperationAdd());
        System.out.println("10 + 5 = " + context.executeStrategy(10, 5));

        context = new Context(new OperationSubtract());
        System.out.println("10 - 5 = " + context.executeStrategy(10, 5));

        // context = new Context(new OperationMultiply());
        Object operationMultiply = testReflect(cacheConfig.getClassName());
        context = new Context((Strategy) operationMultiply);
        System.out.println("10 * 5 = " + context.executeStrategy(10, 5));

    }

    public Object testReflect(String className) {
        try {
            Class<?> c = Class.forName(className);
            Constructor<?> constructor = c.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Test
    public void testProp() {
        System.out.println("=================>" + cacheConfig.getClassName());
    }

}
