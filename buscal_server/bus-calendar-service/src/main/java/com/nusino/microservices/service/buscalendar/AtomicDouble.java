/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.service.buscalendar;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;

public final class AtomicDouble {
    private static final long serialVersionUID = 12327722191124184L;

    private final AtomicLong bits;

    public AtomicDouble() {
        this(0.0d);
    }

    public AtomicDouble(double initialValue) {
        bits = new AtomicLong(toLong(initialValue));
    }


    public final boolean compareAndSet(double expect, double update) {
        return bits.compareAndSet(toLong(expect), toLong(update));
    }


    public final void set(double newValue) {
        bits.set(toLong(newValue));
    }

    public final double get() {
        return toDouble(bits.get());
    }

    public final double getAndSet(double newValue) {
        return toDouble(bits.getAndSet(toLong(newValue)));
    }


    public final double accumulateAndGet(double x, DoubleBinaryOperator accumulatorFunction) {
        double prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsDouble(prev, x);
        } while (!compareAndSet(prev, next));
        return next;
    }


    public final double addAndGet(double delta) {
        return toDouble(bits.addAndGet(toLong(delta)));
    }


    public final double decrementAndGet() {
        return addAndGet(-1.0d);
    }


    public final double getAndAccumulate(double x, DoubleBinaryOperator accumulatorFunction) {
        double prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsDouble(prev, x);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final double getAndAdd(double delta) {
        return toDouble(bits.getAndAdd(toLong(delta)));
    }

    public final double getAndDecrement() {
        return getAndAdd(-1.0d);
    }

    public final double getAndIncrement() {
        return getAndAdd(1.0d);
    }

    public final double incrementAndGet() {
        return addAndGet(1.0d);
    }

    public final double getAndUpdate(DoubleUnaryOperator updateFunction) {
        double prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final void lazySet(double newValue) {
        bits.lazySet(toLong(newValue));
    }

    public long longValue() {
        return (long) get();
    }

    public String toString() {
        return Double.toString(get());
    }

    public final double updateAndGet(DoubleUnaryOperator updateFunction) {
        double prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public int intValue() {
        return (int) get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return get();
    }

    private static double toDouble(long l) {
        return longBitsToDouble(l);
    }

    private static long toLong(double delta) {
        return doubleToLongBits(delta);
    }

}