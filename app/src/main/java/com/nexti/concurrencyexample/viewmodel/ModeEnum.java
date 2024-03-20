package com.nexti.concurrencyexample.viewmodel;

public enum ModeEnum {
    JAVA_THREADS(0),
    KOTLIN_COROUTINE(1),
    RX_JAVA(2);

    private Integer value;

    ModeEnum(Integer value) {
        this.value = value;
    }
}
