package com.midasdev.mochat.global.util.assertion;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Assertion<T> {
    private T param;
    private Predicate<T> predicate;

    private Assertion(T param) {
        this.param = param;
    }

    public static <T> Assertion<T> with(T param) {
        return new Assertion<>(param);
    }

    public Assertion<T> setValidation(Predicate<T> predicate) {
        this.predicate = predicate;
        return this;
    }

    public <X extends Throwable> void validateOrThrow(Supplier<X> exceptionSupplier)
            throws X {
        if (predicate == null) {
            throw new IllegalStateException("검증 로직이 없습니다.");
        }
        if (!predicate.test(param)) {
            throw exceptionSupplier.get();
        }
    }

    public void validateOrExecute(Runnable exceptionHandler) {
        if (predicate == null) {
            throw new IllegalStateException("검증 로직이 없습니다.");
        }
        if (!predicate.test(param)) {
            exceptionHandler.run();
        }
    }
}
