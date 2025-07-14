package com.git.hui.offer.util.json;

import java.util.Objects;

/**
 * 整形枚举基类
 */
public interface IntBaseEnum {
    Integer getValue();

    String getDesc();

    /**
     * 根据code获取枚举值
     *
     * @param enumClass
     * @param value
     * @param <E>
     * @return
     */
    static <E extends Enum<?> & IntBaseEnum> E getEnumByCode(Class<E> enumClass, Integer value) {
        if (!Objects.isNull(value) && enumClass != null) {
            E[] enumConstants = enumClass.getEnumConstants();
            if (enumConstants != null && enumConstants.length != 0) {
                for (E e : enumConstants) {
                    if (e.getValue().equals(value)) {
                        return e;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据code获取value
     *
     * @param enumClass
     * @param value
     * @param <E>
     * @return
     */
    static <E extends Enum<?> & IntBaseEnum> String getDescByCode(Class<E> enumClass, Integer value) {
        if (!Objects.isNull(value)) {
            E enumObj = IntBaseEnum.getEnumByCode(enumClass, value);
            if (enumObj == null) {
                return "";
            } else {
                return enumObj.getDesc();
            }
        }
        return "";
    }
}
