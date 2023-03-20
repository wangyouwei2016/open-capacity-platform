package com.open.capacity.ext.mvc;

import com.open.capacity.ext.mvc.decrypt.RequestDecStrategy;
import com.open.capacity.ext.mvc.encryption.ResponseEncStrategy;
import com.open.capacity.ext.mvc.exception.RequestDesRegisterException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ResponseEncStrategyFactory {
    private ResponseEncStrategyFactory(){};
    private static Map<String, ResponseEncStrategy> STRATEGY_MAP = new ConcurrentHashMap<>();

    static void registerStrategy(String strategyType, ResponseEncStrategy strategy){
        if (STRATEGY_MAP.containsKey(strategyType)){
            throw new RequestDesRegisterException(String.format("%s解析策略以及注册,不能重复注册",strategyType));
        }
        STRATEGY_MAP.putIfAbsent(strategyType, strategy);
    }
    static ResponseEncStrategy selectStrategy(String strategyType){
        return STRATEGY_MAP.get(strategyType);
    }
}