package com.open.capacity.ext.iop.dynamic;

public interface DymIopClientFactory<K,T> {
    T createIopClient(K key);
}
