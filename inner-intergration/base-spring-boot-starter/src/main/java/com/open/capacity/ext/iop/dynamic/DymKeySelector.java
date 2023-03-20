package com.open.capacity.ext.iop.dynamic;

public interface DymKeySelector<K,V> {
    V select(K key);
}
