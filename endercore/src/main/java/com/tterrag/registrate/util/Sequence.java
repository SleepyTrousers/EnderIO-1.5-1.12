package com.tterrag.registrate.util;

import java.util.function.Supplier;

public class Sequence<T> {
    
    public static <T> Sequence<T> create() { return new Sequence<>(); }
    
    public Sequence<T> run(Runnable toRun) {
        toRun.run();
        return this;
    }
    
    public Sequence<T> next(Supplier<T> val) {
        val.get();
        return this;
    }
    
    public Sequence<T> next(T val) {
        return this;
    }
}
