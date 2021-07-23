package com.fintek.rxjava;

import org.jetbrains.annotations.Nullable;

/**
 * Created by ChaoShen on 2021/7/23
 */
public class DispatchedRxjavaComponent extends RxjavaComponent {
    @Override
    public void uncaughtException(@Nullable Thread thread, @Nullable Throwable throwable) {
        dispatch2Handler(thread, throwable);
    }
}
