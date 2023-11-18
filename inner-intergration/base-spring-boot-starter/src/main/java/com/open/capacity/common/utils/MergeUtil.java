package com.open.capacity.common.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

public class MergeUtil {
	
	private static final CopyOptions options = CopyOptions.create().setIgnoreNullValue(true).setOverride(false);
	
	private static final CopyOptions optionsAllowOverride = CopyOptions.create().setIgnoreNullValue(true).setOverride(true);
	
	/**
	 * 将srcBean中属性合并到targetBean,忽略null值，非null值不允许覆盖
	 * @param srcBean
	 * @param targetBean
	 * @return
	 */
	public static Object merge(Object srcBean ,Object targetBean) {
		
		BeanUtil.copyProperties(srcBean, targetBean, options);
		return targetBean;
	}
	
	/**
	 * 将srcBean中属性合并到targetBean,忽略null值，非null值允许覆盖
	 * @param srcBean
	 * @param targetBean
	 * @return
	 */
	public static Object mergeAllowOverride(Object srcBean ,Object targetBean) {
		
		BeanUtil.copyProperties(srcBean, targetBean, optionsAllowOverride);
		return targetBean;
	}
	
	
	/**
	 * 
	 * 把sourceList里面的一些属性合并到targetList里面 基于testFunction的条件,合入逻辑实现为biConsumer
	 * @param targetList
	 * @param sourceList
	 * @param testFunction
	 * @param biConsumer
	 * @param <T>
	 * @param <S>
	 * 每个学生赋值他所在的教室
	 * MergeUtil.merge(studentList,classroomList,(stu,room) -> stu.getRoomId() == room.getRoomId(),(stu,room) -> stu.setRoomName(room.getRoomName()));
	 */
	public static <T, S> void merge(List<T> targetList, List<S> sourceList,
			BiFunction<? super T, ? super S, Boolean> testFunction,
			BiConsumer<? super T, ? super S> biConsumer) {
			targetList.forEach((t) -> {
			Optional<S> optional = sourceList.stream().filter(s -> testFunction.apply(t, s)).findFirst();

			if (optional.isPresent()) {
				biConsumer.accept(t, optional.get());
			}
		});
	}

	/**
	 * 
	 * 把sourceList里面的一些item分类合并到targetList的每一个item里面
	 * 
	 * @param targetList
	 * @param sourceList
	 * @param testFunction
	 * @param biConsumer
	 * @param <T>
	 * @param <S>
	 * 为每个教室，赋值属于他的学生
	 * MergeUtil.mergeList(classroomList,studentList,(classroom1, student1) -> classroom1.getRoomId() == student1.getRoomId(),(classroom1, students) -> classroom1.setStudentList(students));
	 */

	public static <T, S> void mergeList(List<T> targetList, List<S> sourceList,
			BiFunction<? super T, ? super S, Boolean> testFunction,
			BiConsumer<? super T, ? super List<S>> biConsumer) {
			targetList.forEach((t) -> {
			List<S> dataList = sourceList.stream().filter(s -> testFunction.apply(t, s)).collect(Collectors.toList());
			Optional<List<S>> optional = Optional.of(dataList);
			if (optional.isPresent()) {
				biConsumer.accept(t, optional.get());
			}
		});

	}
	 
}
