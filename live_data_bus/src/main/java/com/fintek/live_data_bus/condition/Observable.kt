package com.fintek.live_data_bus.condition

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import java.util.concurrent.TimeUnit

/**
 * Created by ChaoShen on 2020/9/10
 */
interface Observable<T> {

    /**
     * 发送消息
     *
     * @param value 内容
     */
    fun post(value: T)

    /**
     * 延迟发送消息，默认使用毫秒计时
     *
     * @param value 发送的消息
     * @param delay 延迟数
     */
    fun postDelay(value: T, delay: Long) { postDelay(value, delay, TimeUnit.MILLISECONDS) }

    /**
     * 延迟发送消息
     *
     * @param value 发送的消息
     * @param delay 延迟数
     * @param timeUnit 数据时间
     */
    fun postDelay(value: T, delay: Long, timeUnit: TimeUnit)

    /**
     * 进程内发送消息，延迟发送，带生命周期，默认使用毫秒计时
     * 如果延时发送消息的时候sender处于非激活状态，消息取消发送
     *
     * @param sender 消息发送者
     * @param value  发送的消息
     * @param delay  延迟毫秒数
     */
    fun postDelay(sender: LifecycleOwner?, value: T, delay: Long) { postDelay(
        sender,
        value,
        delay,
        TimeUnit.MILLISECONDS
    ) }

    /**
     * 进程内发送消息，延迟发送，带生命周期
     * 如果延时发送消息的时候sender处于非激活状态，消息取消发送
     *
     * @param sender 消息发送者
     * @param value  发送的消息
     * @param delay  延迟毫秒数
     * @param timeUnit 数据时间
     */
    fun postDelay(sender: LifecycleOwner?, value: T, delay: Long, timeUnit: TimeUnit)


    /**
     * 注册一个Observer，生命周期感知，自动取消订阅
     *
     * @param owner    LifecycleOwner
     * @param observer 观察者
     */
    fun observe(owner: LifecycleOwner, observer: Observer<T>)

    /**
     * 注册一个Observer，生命周期感知，自动取消订阅
     * 如果之前有消息发送，可以在注册时收到消息（消息同步）
     *
     * @param owner    LifecycleOwner
     * @param observer 观察者
     */
    fun observeSticky(owner: LifecycleOwner, observer: Observer<T>)

    /**
     * 注册一个Observer，需手动解除绑定
     *
     * @param observer 观察者
     */
    fun observeForever(observer: Observer<T>)

    /**
     * 注册一个Observer，需手动解除绑定
     * 如果之前有消息发送，可以在注册时收到消息（消息同步）
     *
     * @param observer 观察者
     */
    fun observeStickyForever(observer: Observer<T>)

    /**
     * 通过observeForever或observeStickyForever注册的，需要调用该方法取消订阅
     *
     * @param observer 观察者
     */
    fun removeObserver(observer: Observer<T>)
}