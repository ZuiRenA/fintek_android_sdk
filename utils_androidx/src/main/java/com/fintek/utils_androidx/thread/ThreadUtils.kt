package com.fintek.utils_androidx.thread

import android.os.Handler
import android.os.Looper
import androidx.annotation.CallSuper
import androidx.annotation.IntRange
import com.fintek.utils_androidx.UtilsBridge
import java.util.*
import java.util.concurrent.ConcurrentHashMap
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

    private val TYPE_PRIORITY_POOLS: MutableMap<Int, MutableMap<Int, ExecutorService>> = HashMap()

    private val TASK_POOL_MAP: MutableMap<Task<*>, ExecutorService> = ConcurrentHashMap()

    @JvmStatic
    fun getMainHandler() = HANDLER

    @JvmStatic
    fun runOnUiThread(runnable: Runnable) {
        if (isMainThread) runnable.run()
        else HANDLER.post(runnable)
    }

    @JvmStatic
    fun runOnUiThreadDelay(runnable: Runnable, delayMillis: Long) {
        HANDLER.postDelayed(runnable, delayMillis)
    }


    /**
     * Return a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.
     *
     * @param size The size of thread in the pool.
     * @param priority The priority of thread in the poll.
     * @return a fixed thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getFixedPool(
        @IntRange(from = 1) size: Int,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService = getPool(size)

    /**
     * Return a thread pool that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed.
     *
     * @param priority The priority of thread in the poll.
     * @return a single thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getSinglePool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService = getPool(TYPE_SINGLE, priority)


    /**
     *
     * Return a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.
     *
     * @return a cached thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getCachedPool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService = getPool(TYPE_CACHE, priority)


    /**
     * Return a thread pool that creates (2 * CPU_COUNT + 1) threads
     * operating off a queue which size is 128.
     *
     * @param priority The priority of thread in the poll.
     * @return a IO thread pool
     */
    @JvmStatic
    @JvmOverloads
    fun getIoPool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService = getPool(TYPE_IO, priority)

    /**
     * Return a thread pool that creates (CPU_COUNT + 1) threads
     * operating off a queue which size is 128 and the maximum
     * number of threads equals (2 * CPU_COUNT + 1).
     *
     * @param priority The priority of thread in the poll.
     * @return a cpu thread pool for
     */
    @JvmStatic
    @JvmOverloads
    fun getCpuPool(
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ): ExecutorService = getPool(TYPE_CPU, priority)


    /**
     * Executes the given task in a fixed thread pool.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixed(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPool(size, priority), task)
    }

    /**
     * Executes the given task in a fixed thread pool after the given delay.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixedWithDelay(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPool(size, priority), task, delay, unit)
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size     The size of thread in the fixed thread pool.
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(size, priority), task, 0, period, unit)
    }

    /**
     * Executes the given task in a fixed thread pool at fix rate.
     *
     * @param size         The size of thread in the fixed thread pool.
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param T            The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByFixedAtFixRate(
        @IntRange(from = 1) size: Int,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(size, priority), task, initialDelay, period, unit)
    }

    /**
     * Executes the given task in a single thread pool.
     *
     * @param task The task to execute.
     * @param priority The priority of thread in the poll.
     * @param T  The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingle(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPool(TYPE_SINGLE, priority), task)
    }

    /**
     * Executes the given task in a single thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingleWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPool(TYPE_SINGLE, priority), task, delay, unit)
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
    */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(TYPE_SINGLE, priority), task, 0, period, unit)
    }

    /**
     * Executes the given task in a single thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param T            The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeBySingleAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(TYPE_SINGLE, priority), task, initialDelay, period, unit)
    }

    /**
     * Executes the given task in a cached thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCached(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPool(TYPE_CACHE, priority), task)
    }


    /**
     * Executes the given task in a cached thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCachedWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPool(TYPE_CACHE, priority), task, delay, unit)
    }


    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(TYPE_CACHE, priority), task, 0, period, unit)
    }

    /**
     * Executes the given task in a cached thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param T            The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCachedAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(TYPE_CACHE, priority), task, initialDelay, period, unit)
    }

    /**
     * Executes the given task in an IO thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIo(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPool(TYPE_IO, priority), task)
    }

    /**
     * Executes the given task in an IO thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIoWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPool(TYPE_IO, priority), task, delay, unit)
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(TYPE_IO, priority), task, 0, period, unit)
    }

    /**
     * Executes the given task in an IO thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param T            The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByIoAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(TYPE_IO, priority), task, initialDelay, period, unit)
    }

    /**
     * Executes the given task in a cpu thread pool.
     *
     * @param task     The task to execute.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpu(
        task: Task<T>,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        execute(getPool(TYPE_CPU, priority), task)
    }

    /**
     * Executes the given task in a cpu thread pool after the given delay.
     *
     * @param task     The task to execute.
     * @param delay    The time from now to delay execution.
     * @param unit     The time unit of the delay parameter.
     * @param priority The priority of thread in the poll.
     * @param T      The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpuWithDelay(
        task: Task<T>,
        delay: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeWithDelay(getPool(TYPE_CPU, priority), task, delay, unit)
    }

    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task     The task to execute.
     * @param period   The period between successive executions.
     * @param unit     The time unit of the period parameter.
     * @param priority The priority of thread in the poll.
     * @param T        The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int
    ) {
        executeAtFixedRate(getPool(TYPE_CPU, priority), task, 0, period, unit)
    }


    /**
     * Executes the given task in a cpu thread pool at fix rate.
     *
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param priority     The priority of thread in the poll.
     * @param T            The type of the task's result.
     */
    @JvmStatic
    @JvmOverloads
    fun <T> executeByCpuAtFixRate(
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit,
        @IntRange(from = 1, to = 10) priority: Int = Thread.NORM_PRIORITY
    ) {
        executeAtFixedRate(getPool(TYPE_CPU, priority), task, initialDelay, period, unit)
    }

    /**
     * Executes the given task in a custom thread pool.
     *
     * @param pool The custom thread pool.
     * @param task The task to execute.
     * @param T  The type of the task's result.
     */
    @JvmStatic
    fun <T> executeByCustom(
        pool: ExecutorService,
        task: Task<T>
    ) {
        execute(pool, task)
    }

    /**
     * Executes the given task in a custom thread pool after the given delay.
     *
     * @param pool  The custom thread pool.
     * @param task  The task to execute.
     * @param delay The time from now to delay execution.
     * @param unit  The time unit of the delay parameter.
     * @param T     The type of the task's result.
     */
    @JvmStatic
    fun <T> executeByCustomWithDelay(
        pool: ExecutorService,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        executeWithDelay(pool, task, delay, unit)
    }

    /**
     * Executes the given task in a custom thread pool at fix rate.
     *
     * @param pool   The custom thread pool.
     * @param task   The task to execute.
     * @param period The period between successive executions.
     * @param unit   The time unit of the period parameter.
     * @param T      The type of the task's result.
     */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService,
        task: Task<T>,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, 0, period, unit)
    }

    /**
     * Executes the given task in a custom thread pool at fix rate.
     *
     * @param pool         The custom thread pool.
     * @param task         The task to execute.
     * @param initialDelay The time to delay first execution.
     * @param period       The period between successive executions.
     * @param unit         The time unit of the initialDelay and period parameters.
     * @param T          The type of the task's result.
     */
    fun <T> executeByCustomAtFixRate(
        pool: ExecutorService,
        task: Task<T>,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        executeAtFixedRate(pool, task, initialDelay, period, unit)
    }

    /**
     * Cancel the tasks in pool
     * @param executorService the pool
     */
    @JvmStatic
    fun cancel(executorService: ExecutorService) {
        if (executorService is ThreadPoolExecutor4Util) {
            TASK_POOL_MAP.forEach { (task, taskExecutorService) ->
                if (executorService == taskExecutorService) {
                    cancel(task)
                }
            }
        } else {
            UtilsBridge.e("ThreadUtils", "The executorService is not ThreadUtils's pool.")
        }
    }

    /**
     * Cancel task
     * @param task the task to cancel
     */
    @JvmStatic
    fun cancel(task: Task<*>) {
        task.cancel()
    }

    /**
     * Cancel tasks
     * @param tasks the tasks to cancel
     */
    @JvmStatic
    fun cancel(vararg tasks: Task<*>) {
        if (tasks.isNullOrEmpty()) return

        tasks.forEach { it.cancel() }
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

    private fun <T> executeWithDelay(
        pool: ExecutorService,
        task: Task<T>,
        delay: Long,
        unit: TimeUnit
    ) {
        execute(pool, task, delay, 0, unit)
    }

    private fun <T> executeAtFixedRate(
        pool: ExecutorService,
        task: Task<T>,
        delay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        execute(pool, task, delay, period, unit)
    }

    private fun <T> execute(
        pool: ExecutorService,
        task: Task<T>,
        delay: Long,
        period: Long,
        unit: TimeUnit
    ) {
        synchronized(TASK_POOL_MAP) {
            if (TASK_POOL_MAP[task] != null) {
                UtilsBridge.e("ThreadUtils", "Task can only be executed once.")
                return
            }

            TASK_POOL_MAP[task] = pool
        }

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

    private fun getPool(type: Int, priority: Int = Thread.NORM_PRIORITY): ExecutorService {
        synchronized(TYPE_PRIORITY_POOLS) {
            var priorityPools = TYPE_PRIORITY_POOLS[type]

            var pool: ExecutorService?
            if (priorityPools == null) {
                priorityPools = ConcurrentHashMap()
                pool = createPool(type, priority)
                priorityPools[priority] = pool
                TYPE_PRIORITY_POOLS[type] = priorityPools
            } else {
                pool = priorityPools[priority]
                if (pool == null) {
                    pool = createPool(type, priority)
                    priorityPools[priority] = pool
                }
            }

            return pool
        }
    }
}

abstract class SimpleTask<T> : Task<T>() {

    override fun onCancel() {
        UtilsBridge.e("ThreadUtils", "onCancel: ${Thread.currentThread()}")
    }

    override fun onFail(t: Throwable) {
        UtilsBridge.e("ThreadUtils", "onFail: $t")
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
                    UtilsBridge.w("ThreadUtils", "Scheduled task doesn't support timeout.")
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
                        timeoutListener?.onTimeout()
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