package com.fintek.throwable.rxjava;

import com.fintek.throwable.base.BaseThreadUncaughtExceptionComponent;
import com.fintek.throwable.base.DispatchedComponentHandler;

import org.jetbrains.annotations.Nullable;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * Created by ChaoShen on 2021/7/23
 */
public abstract class RxjavaComponent implements
        DispatchedComponentHandler, Consumer<Throwable> {
    @Override
    public void dispatch2Handler(@Nullable Thread thread, @Nullable Throwable throwable) {
        BaseThreadUncaughtExceptionComponent.INSTANCE.uncaughtException(thread, throwable);
    }

    @Override
    public void accept(Throwable throwable) {
        Thread currentThread = Thread.currentThread();
        uncaughtException(currentThread, throwable);
    }
}
