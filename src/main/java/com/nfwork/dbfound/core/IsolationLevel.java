package com.nfwork.dbfound.core;

public enum IsolationLevel {
    DEFAULT(0),
    READ_UNCOMMITTED(1),
    READ_COMMITTED(2),
    REPEATABLE_READ(4),
    SERIALIZABLE(8);

    private final int value;

    private IsolationLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
