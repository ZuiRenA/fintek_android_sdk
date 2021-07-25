package com.fintek.throwable.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

/**
 * Created by ChaoShen on 2021/7/23
 */
public class RxjavaComponentBinder {
    private RxjavaComponentBinder() {
    }

    private static class Lazy {
        private static final RxjavaComponent RxjavaComponentBinder = new DispatchedRxjavaComponent();
    }

    public static void setErrorHandler() {
        RxJavaPlugins.setErrorHandler(Lazy.RxjavaComponentBinder);
    }

    public static <T> Observable<T> onErrorReturn(Observable<T> observableSource) {
        return observableSource.onErrorReturn(throwable -> {
            RxJavaPlugins.onError(throwable);
            return null;
        });
    }
}
