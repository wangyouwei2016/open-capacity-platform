package com.open.capacity.db.service;

import java.io.Serializable;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.open.capacity.common.utils.CharsetKit;
import com.open.capacity.common.utils.ConvertUtils;
import com.open.capacity.db.support.CustomUpdateWrapper;

public interface IService<T> extends com.baomidou.mybatisplus.extension.service.IService<T> {

	/**
	 * 通过主键ID自增指定列
	 * 自定义步长
	 * @param id    主键ID
	 * @param field DO实体类属性字段
	 * @param step  步长
	 * @return 更新成功 返回true
	 */
	default boolean increaseById(Serializable id, SFunction<T, ? extends Number> field, int step) {
		CustomUpdateWrapper<T> wrapper = new CustomUpdateWrapper<T>();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
		String filedName = CharsetKit.getFiledName(field);
		String columnName = ConvertUtils.toUnderScoreCase(filedName);
		wrapper.incr(columnName, step).eq(tableInfo.getKeyColumn(), id);
		return this.update(wrapper);
	}

	/**
	 * <p>
	 * 通过主键ID自减指定列
	 * @param id    主键ID
	 * @param field DO实体类属性字段
	 * @param step  步长
	 * @return 更新成功 返回true
	 */
	default boolean decreaseById(Serializable id, SFunction<T, ? extends Number> field, int step) {
		CustomUpdateWrapper<T> wrapper = new CustomUpdateWrapper<T>();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
		String filedName = CharsetKit.getFiledName(field);
		String columnName = ConvertUtils.toUnderScoreCase(filedName);
		wrapper.decr(columnName, step).eq(tableInfo.getKeyColumn(), id);
		return this.update(wrapper);
	}
	
}
