package com.honezhi.sso.server.controller.strategy_pattern;

/**
 * @author： tsb
 * @date： 2020/8/17
 * @description：
 * @modifiedBy：
 * @version: 1.0
 */
public class Context {

    private Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public int executeStrategy(int num1, int num2) {
        return strategy.doOperation(num1, num2);
    }

}
