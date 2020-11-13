package com.open.capacity.rabbitmq.comsumer;



import com.open.capacity.rabbitmq.common.DetailResponse;


public interface MessageProcess<T> {
    DetailResponse process(T message);
}
