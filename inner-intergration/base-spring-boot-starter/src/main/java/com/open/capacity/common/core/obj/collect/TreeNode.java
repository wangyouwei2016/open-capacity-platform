package com.open.capacity.common.core.obj.collect;

import java.util.List;

public interface TreeNode<K,N> {
    K getParentKey();
    K getKey();
    void setChildren(List<N> children);
}
