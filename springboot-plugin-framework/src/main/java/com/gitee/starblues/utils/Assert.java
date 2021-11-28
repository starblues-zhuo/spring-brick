package com.gitee.starblues.utils;


import sun.awt.geom.AreaOp;

import java.util.function.Supplier;

/**
 * @author starBlues
 * @version 3.0.0
 */
public abstract class Assert {

    private Assert(){};

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(nullSafeGet(messageSupplier));
        }
    }

    public static <T> T isNotNull(T t, String message) {
        if (t == null) {
            throw new IllegalArgumentException(message);
        }
        return t;
    }

    public static <T> T isNotNull(T t, Supplier<String> messageSupplier) {
        if (t == null) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
        return t;
    }

    public static <T> T isNotEmpty(T t, String message) {
        if (ObjectUtils.isEmpty(t)) {
            throw new IllegalArgumentException(message);
        }
        return t;
    }

    public static <T> T isNotEmpty(T t, Supplier<String> messageSupplier) {
        if (ObjectUtils.isEmpty(t)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
        return t;
    }

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }

}
