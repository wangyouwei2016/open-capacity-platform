/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock;

public class DefaultMockChecker implements MockChecker{
    @Override
    public boolean needMock(MockHandler mockHandler) {
        return true;
    }
}
