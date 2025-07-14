package com.git.hui.offer.util.json;

import java.util.Objects;

public interface StringBaseEnum {
    String getValue();

    String getDesc();
    /**
     * 根据code获取枚举值
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    static  <E extends Enum<?> & StringBaseEnum> E getEnumByCode(Class<E> enumClass, String code) {
        if (!Objects.isNull(code)) {
            E[] enumConstants = enumClass.getEnumConstants();
            for (E e : enumConstants) {
                if (e.getValue().equals(code)) {
                    return e;
                }
            }
        }
        return null;
    }
    /**
     * 根据code获取value
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    static <E extends Enum<?> & StringBaseEnum> String getValueByCode(Class<E> enumClass, String code) {
        if (!Objects.isNull(code)) {
            E enumObj = StringBaseEnum.getEnumByCode(enumClass, code);
            if (enumObj == null) {
                return "";
            } else {
                return enumObj.getDesc();
            }
        }
        return "";
    }
}