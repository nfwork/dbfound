package com.nfwork.dbfound.model.enums;

public interface BaseEnum<E extends Enum<?>, T> {
    /**
     * 获取枚举的值
     *
     * @return 枚举的值
     */
    T getValue();
}
