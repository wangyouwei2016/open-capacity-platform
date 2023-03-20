package com.open.capacity.ext.mock;

public class DefaultMockChecker implements MockChecker{
    @Override
    public boolean needMock(MockHandler mockHandler) {
        return true;
    }
}
