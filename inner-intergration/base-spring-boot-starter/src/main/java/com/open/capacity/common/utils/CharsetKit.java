package com.open.capacity.common.utils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.ibatis.reflection.property.PropertyNamer;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * 字符集工具类
 */
public class CharsetKit {
    /**
     * ISO-8859-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * UTF-8
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * GBK
     */
    public static final String GBK = "GBK";

    /**
     * ISO-8859-1
     */
    public static final Charset CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
    /**
     * UTF-8
     */
    public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
    /**
     * GBK
     */
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    /**
     * 转换为Charset对象
     *
     * @param charset 字符集，为空则返回默认字符集
     * @return Charset
     */
    public static Charset charset(String charset) {
        return StringUtil.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * 转换字符串的字符集编码
     *
     * @param source      字符串
     * @param srcCharset  源字符集，默认ISO-8859-1
     * @param destCharset 目标字符集，默认UTF-8
     * @return 转换后的字符集
     */
    public static String convert(String source, String srcCharset, String destCharset) {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * 转换字符串的字符集编码
     *
     * @param source      字符串
     * @param srcCharset  源字符集，默认ISO-8859-1
     * @param destCharset 目标字符集，默认UTF-8
     * @return 转换后的字符集
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset) {
        if (null == srcCharset) {
            srcCharset = StandardCharsets.ISO_8859_1;
        }

        if (null == destCharset) {
            destCharset = StandardCharsets.UTF_8;
        }

        if (StringUtil.isEmpty(source) || srcCharset.equals(destCharset)) {
            return source;
        }
        return new String(source.getBytes(srcCharset), destCharset);
    }

    /**
     * @return 系统字符集编码
     */
    public static String systemCharset() {
        return Charset.defaultCharset().name();
    }
    
    @SuppressWarnings("unchecked")
    public static <T, R> R getFiledValue(T t, SFunction<T, R> action) {
        String fieldName = Optional.ofNullable(getFiledName(action)).orElse("");
        try {
            Field field = t.getClass().getField(fieldName);
            return (R) field.get(t);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过反射 给属性赋值
     *
     * @param t
     * @param action
     * @param value
     * @param <S>
     * @param <RR>
     */
    public static <T, S, RR> void setFiledValue(T t, SFunction<S, RR> action, Object value) {
        String fieldName = Optional.ofNullable(getFiledName(action)).orElse("");
        try {
            Field field = t.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(t, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setFiledValue(T t, String fieldName, Object value) {
        try {
            Field field = t.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(t, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setFiledValue(T t, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(t, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过方法引用获取指定实体类的字段名（属性名）
     *
     * @param action
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> String getFiledName(SFunction<T, R> action) {
        return Optional.ofNullable(action).map(LambdaUtils::extract)
            .map(LambdaMeta::getImplMethodName)
            .map(PropertyNamer::methodToProperty).orElse(null);
    }

    // /**
    //  * 通过方法引用获取指定实体类的字段名（属性名）
    //  *
    //  * @param <T>
    //  * @param <R>
    //  * @param action
    //  * @return
    //  */
    // @SafeVarargs
    // public static <T, R> List<String> getFiledNames(SFunction<T, R>... action) {
    //     return Arrays.stream(action).map(LambdaUtils::extract)
    //         .map(LambdaMeta::getImplMethodName)
    //         .map(PropertyNamer::methodToProperty)
    //         .collect(Collectors.toList());
    // }


    @SafeVarargs
    public static <T> List<String> getFiledNames(SFunction<T, ? extends Serializable>... action) {
        return Arrays.stream(action).map(LambdaUtils::extract)
            .map(LambdaMeta::getImplMethodName)
            .map(PropertyNamer::methodToProperty)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object entity, String fieldName) {
        return (T) ReflectionKit.getFieldValue(entity, fieldName);
    }


    public static Object getFieldValue(Object entity, Field field) {
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通过Clazz对象创建实例
     *
     * @param clazz CLass对象
     * @param <T>   泛型
     * @return 泛型实例
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得参数为{@code doClazz}的构造器
     *
     * @param doClazz DO实体类的CLass对象实例
     * @param voClazz VO实体类的CLass对象实例
     * @param <VO>    VO实体类泛型
     * @return VO实体类的构造器
     */
    // @SafeVarargs
    public static <VO> Constructor<VO> getConstructor(Class<VO> voClazz, Class<?>... doClazz) {
        Objects.requireNonNull(doClazz);
        try {
            return voClazz.getConstructor(doClazz);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过构造器创建对象
     *
     * @param constructor 以泛型{@code VO}为类型的构造器实例
     * @param initargs    以泛型{@code DO}为类型的参数实例
     * @param <DO>        {@code DO}泛型
     * @param <VO>        {@code VO}泛型
     * @return 以泛型{@code VO}为类型的对象实例
     */
    @SafeVarargs
    public static <DO, VO extends DO> VO newInstance(Constructor<VO> constructor, DO... initargs) {
        try {
            return constructor.newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>显示化获得{@code Class<T>}对象的类型</p>
     * <p>本方法的作用时避免在显示强转时出现<i>未检查警告</i></p>
     * <p>注意{@code Class<\?>}与{@code Class<T>}是同一个类型才能强转</p>
     *
     * @param clazz Class对象实例
     * @param <T>   元素类型
     * @return 如果参数<code>clazz</code>不为<code>null</code>，则返回强转后的对象，否则返回<code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(Class<?> clazz) {
        return (Class<T>) clazz;
    }

}
