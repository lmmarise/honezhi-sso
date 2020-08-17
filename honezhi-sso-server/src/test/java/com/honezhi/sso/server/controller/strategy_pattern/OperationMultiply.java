package com.honezhi.sso.server.controller.strategy_pattern;

/**
 * @author： tsb
 * @date： 2020/8/17
 * @description：
 * @modifiedBy：
 * @version: 1.0
 */
public class OperationMultiply implements Strategy {
    @Override
    public int doOperation(int num1, int num2) {
        return num1 * num2;
    }
}
