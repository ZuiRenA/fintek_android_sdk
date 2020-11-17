package com.fintek.utils_androidx.thread

import android.os.Handler
import android.os.Looper
import androidx.annotation.CallSuper
import com.fintek.utils_androidx.UtilsBridge
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.timerTask

/**
 * Created by ChaoShen on 2020/11/17
 */
object ThreadUtils {
    val isMainThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()

    private val HANDLER: Handler    = Handler(Looper.getMainLooper())
    private val TIMER: Timer        = Timer()
    private var sDeliver: Executor? = null


    private val singlePool: ExecutorService by lazy { createSinglePool(Thread.NORM_PRIORITY) }

    fun getMainHandler() = HANDLER

    fun runOnUiThread(runnable: Runnable) {
        if (isMainThread) runnable.run()
        else HANDLER.post(runnable)
    }

    fun <T> executeBySingle(task: Task<T>) {
        execute(singlePool, task)
    }

    @Synchronized
    internal fun getGlobalDeliver(): Executor {
        if (sDeliver == null) {
            sDeliver = Executor {
                runOnUiThread(it)
            }
        }

        return checkNotNull(sDeliver)
    }

    private fun <T> execute(pool: ExecutorService, task: Task<T>) {
        execute(pool, task, 0, 0, TimeUnit.MILLISECONDS)
    }

    private fun <T> execute(
        pool: ExecutorService,
        task: Task<T>,
        delay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        if (period == 0L) {
            if (delay == 0L) {
                pool.execute(task)
            } else {
                val timerTask: TimerTask = timerTask { pool.execute(task) }
                TIMER.schedule(timerTask, unit.toMillis(delay))
            }
        } else {
            task.setSchedule(true)
            val timerTask = timerTask { pool.execute(task) }
            TIMER.scheduleAtFixedRate(timerTask, unit.toMillis(delay), unit.toMillis(period))
        }
    }
}

abstract class SimpleTask<T> : Task<T>() {

    override fun onCancel() {
        UtilsBridge.e("onCancel: ${Thread.currentThread()}")
    }

    override fun onFail(t: Throwable) {
        UtilsBridge.e("onFail: $t")
    }
}

abstract class Task<T> : Runnable {
    private val state = AtomicInteger(NEW)

    @Volatile private var isSchedule: Boolean = false
    @Volatile private var runner: Thread? = null

    private var timer: Timer? = null
    private var timeoutMillis: Long = 0
    private var timeoutListener: OnTimeoutListener? = null

    private var deliver: Executor? = null

    val isDone: Boolean get() = state.get() > RUNNING

    val isCanceled: Boolean get() = state.get() >= CANCELLED

    private val requiredDeliver: Executor get() {
        if (deliver == null) {
            return ThreadUtils.getGlobalDeliver()
        }
        return checkNotNull(deliver)
    }


    /**
     * Thread is Child thread
     */
    @Throws(Throwable::class)
    abstract fun doInBackground(): T

    /**
     * Thread is Main Thread
     */
    abstract fun onSuccess(result: T)

    abstract fun onCancel()

    abstract fun onFail(t: Throwable)

    override fun run() {
        if (isSchedule) {
            if (runner == null) {
                if (!state.compareAndSet(NEW, RUNNING)) return
                runner = Thread.currentThread()
                if (timeoutListener != null) {
                    UtilsBridge.w("Scheduled task doesn't support timeout.")
                }
            } else {
                if (state.get() != RUNNING) return
            }
        } else {
            if (!state.compareAndSet(NEW, RUNNING)) return
            runner = Thread.currentThread()
            timeoutListener?.let {
                timer = Timer()
                checkNotNull(timer).schedule(timerTask {
                    if (!isDone && timeoutListener != null) {
                        timeout()
                    }
                }, timeoutMillis)
            }
        }

        try {
            val result: T = doInBackground()
            if (isSchedule) {
                if (state.get() != RUNNING) return
                requiredDeliver.execute {
                    onSuccess(result)
                }
            } else {
                if (!state.compareAndSet(RUNNING, COMPLETING)) return
                requiredDeliver.execute {
                    onSuccess(result)
                    onDone()
                }
            }
        } catch (ignore: InterruptedException) {
            state.compareAndSet(CANCELLED, INTERRUPTED)
        } catch (t: Throwable) {
            if (!state.compareAndSet(RUNNING, EXCEPTIONAL)) return
            requiredDeliver.execute {
                onFail(t)
                onDone()
            }
        }
    }

    @JvmOverloads
    fun cancel(mayInterruptIfRunning: Boolean = true) {
        synchronized(state) {
            if (state.get() > RUNNING) return
            state.set(CANCELLED)
        }

        if (mayInterruptIfRunning) {
            if (runner != null) runner?.interrupt()
        }

        requiredDeliver.execute {
            onCancel()
            onDone()
        }
    }

    fun setDeliver(deliver: Executor) = apply {
        this.deliver = deliver
    }

    /**
     * Scheduled task doesn't support timeout.
     */
    fun setTimeout(timeoutMillis: Long, listener: OnTimeoutListener) = apply {
        this.timeoutMillis = timeoutMillis
        this.timeoutListener = listener
    }

    @CallSuper
    protected fun onDone() {
        if (timer != null) {
            timer?.cancel()
            timer = null
            timeoutListener = null
        }
    }

    internal fun setSchedule(isSchedule: Boolean) {
        this.isSchedule = isSchedule
    }

    private fun timeout() {
        synchronized(state) {
            if (state.get() > RUNNING) return
            state.set(TIMEOUT)
        }

        runner?.interrupt()
        onDone()
    }

    interface OnTimeoutListener {
        fun onTimeout()
    }

    companion object {
        private const val NEW         = 0
        private const val RUNNING     = 1
        private const val EXCEPTIONAL = 2
        private const val COMPLETING  = 3
        private const val CANCELLED   = 4
        private const val INTERRUPTED = 5
        private const val TIMEOUT     = 6
    }
}