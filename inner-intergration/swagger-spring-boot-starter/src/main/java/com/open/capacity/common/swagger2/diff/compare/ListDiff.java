package com.open.capacity.common.swagger2.diff.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.Lists;

/**
 * compare two Lists
 *
 * @author Sayi
 * @version
 */
public class ListDiff<K> {

    private List<K> increased;
    private List<K> missing;
    private Map<K, K> shared;

    private ListDiff() {
        this.shared = new HashMap<>();
    }

    public static <K> ListDiff<K> diff(List<K> left, List<K> right) {
        return diff(left, right, (t, u) -> u);
    }

    /**
     *
     * @param left
     * @param right
     * @param biFunc
     *            if right List contains left element
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K> ListDiff<K> diff(List<K> left, List<K> right, BiFunction<List<K>, K, K> biFunc) {
        ListDiff<K> instance = new ListDiff<>();
        if (null == left && null == right) return instance;
        if (null == left) {
            instance.increased = right;
            return instance;
        }
        if (null == right) {
            instance.missing = left;
            return instance;
        }

        instance.increased = new ArrayList<>(right);
        instance.missing = new ArrayList<>();
        for (K ele : left) {
            K rightEle = biFunc.apply(right, ele);
            if (null != rightEle) {
                ListIterator<K> listIterator = instance.increased.listIterator();
                while (listIterator.hasNext()) {
                    K k = listIterator.next();
                    if (biFunc.apply(Lists.newArrayList(k), ele) != null) {
                        listIterator.remove();
                        break;
                    }
                }
                instance.shared.put(ele, rightEle);
            } else {
                instance.missing.add(ele);
            }
        }
        return instance;
    }

    public List<K> getIncreased() {
        return increased;
    }

    public List<K> getMissing() {
        return missing;
    }

    public Map<K, K> getShared() {
        return shared;
    }

}
