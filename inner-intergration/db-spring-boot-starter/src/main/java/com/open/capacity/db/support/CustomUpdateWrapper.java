package com.open.capacity.db.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

/**
 * <p>自定义更新包装器</p>
 **/
public class CustomUpdateWrapper<T> extends UpdateWrapper<T> {

    /**
     * 通过构造器创建{@code CustomLambdaUpdateWrapper}更新包装器实例
     *
     * @param entity 实体类对象
     */
    public CustomUpdateWrapper(T entity) {
        super(entity);
    }

    /**
     * 通过构造器创建{@code CustomLambdaUpdateWrapper}更新包装器实例
     */
    public CustomUpdateWrapper() {
        super();
    }

    /**
     * <p>指定列自增 默认自增长值为<i>1</i></p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     *
     * @param column 列引用
     */
    public CustomUpdateWrapper<T> incr(String column) {
        return incr(column, 1);
    }

    /**
     * <p>指定列自增 自定义增长值</p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     * <p>自增长值不宜过大 合适选择增长值</p>
     *
     * @param column 数据库列字段
     * @param value  增长值 增长值小于或者等于0 将忽略本次更新
     */
    public CustomUpdateWrapper<T> incr(String column, int value) {
        int max = Math.max(value, 0);
        super.setSql(max > 0, String.format("%s =  %s + %d", column, column, max));
        return this;
    }

    /**
     * <p>指定列自减 默认自减少值为<i>1</i></p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     *
     * @param column 数据库列字段
     */
    public CustomUpdateWrapper<T> decr(String column) {
        return decr(column, 1);
    }

    /**
     * <p>指定列自减 自定义自减值</p>
     * <p>需选用具有具备四则运算的数字类型类 不可选择字符串列</p>
     * <p>自减少值不宜过大 合适选择增长值</p>
     *
     * @param column 数据库列字段
     * @param value  减少值 减少值小于或者等于0 将忽略本次更新
     */
    public CustomUpdateWrapper<T> decr(String column, int value) {
        int max = Math.max(value, 0);
        super.setSql(max > 0, String.format("%s =  %s - %d", column, column, max));
        return this;
    }
}
