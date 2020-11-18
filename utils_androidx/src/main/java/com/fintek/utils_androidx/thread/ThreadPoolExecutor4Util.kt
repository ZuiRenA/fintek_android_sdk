package com.fintek.utils_androidx.thread

import com.fintek.utils_androidx.UtilsBridge
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

internal const val TYPE_SINGLE = -1
internal const val TYPE_CACHE  = -2
internal const val TYPE_IO     = -4
internal const val TYPE_CPU    = -8

internal val CPU_COUNT by lazy { Runtime.getRuntime().availableProcessors() }

internal fun createPool(type: Int, priority: Int): ExecutorService = when(type) {
    TYPE_SINGLE -> createSinglePool(priority)
    TYPE_CACHE -> createCachePool(priority)
    TYPE_IO -> createIOPool(priority)
    TYPE_CPU -> createCPUPool(priority)
    else -> createOtherPool(type, priority)
}

private fun createSinglePool(priority: Int) = ThreadPoolExecutor4Util(
    1, 1,
    0L, TimeUnit.MILLISECONDS,
    LinkedBlockingQueue4Util(), UtilsThreadFactory("single", priority = priority)
)

private fun createCachePool(priority: Int) = ThreadPoolExecutor4Util(
    0, 128,
    60L, TimeUnit.SECONDS,
    LinkedBlockingQueue4Util(true), UtilsThreadFactory("cached", priority)
)

private fun createIOPool(priority: Int) = ThreadPoolExecutor4Util(
    2 * CPU_COUNT + 1, 2 * CPU_COUNT + 1,
    30, TimeUnit.SECONDS,
    LinkedBlockingQueue4Util(), UtilsThreadFactory("io", priority)
)

private fun createCPUPool(priority: Int) = ThreadPoolExecutor4Util(
    CPU_COUNT + 1, 2 * CPU_COUNT + 1,
    30, TimeUnit.SECONDS,
    LinkedBlockingQueue4Util(true), UtilsThreadFactory("cpu", priority)
)

private fun createOtherPool(type: Int, priority: Int) = ThreadPoolExecutor4Util(
    type, type,
    0, TimeUnit.SECONDS,
    LinkedBlockingQueue4Util(), UtilsThreadFactory("fixed($type)", priority)
)


internal class ThreadPoolExecutor4Util(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit?,
    workQueue: LinkedBlockingQueue4Util?,
    threadFactory: ThreadFactory?
) : ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    threadFactory
) {

    private val submittedCount = AtomicInteger()

    private val workQueue: LinkedBlockingQueue4Util? = let {
        workQueue?.pool = this
        workQueue
    }

    internal fun getSubmittedCount() = submittedCount.get()

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        submittedCount.decrementAndGet()
        super.afterExecute(r, t)
    }

    override fun execute(command: Runnable?) {
        if (isShutdown) return
        submittedCount.incrementAndGet()
        try {
            super.execute(command)
        } catch (ignore: RejectedExecutionException) {
            UtilsBridge.e("ThreadUtils: This will not happen!")
            workQueue?.offer(command)
        } catch (t: Throwable) {
            submittedCount.decrementAndGet()
        }
    }
}

internal class LinkedBlockingQueue4Util @JvmOverloads constructor(
    isAddSubThreadFirstThenAddQueue: Boolean = false,
    capacity: Int = Int.MAX_VALUE,
) : LinkedBlockingQueue<Runnable>() {
    private val capacity: Int = if (isAddSubThreadFirstThenAddQueue) 0 else capacity
    internal var pool: ThreadPoolExecutor4Util? = null

    override fun offer(e: Runnable?): Boolean {
        if (capacity <= size && pool != null && pool!!.poolSize < pool!!.maximumPoolSize) {
            // create a non-core thread
            return false
        }

        return super.offer(e)
    }
}

internal class UtilsThreadFactory @JvmOverloads constructor(
    prefix: String,
    private val priority: Int,
    private val isDaemon: Boolean = false
) : AtomicLong(), ThreadFactory {
    companion object {
        private val POOL_NUMBER = AtomicInteger(1)
    }

    private val namPrefix = "$prefix-pool-${POOL_NUMBER.getAndIncrement()}-thread-"


    override fun newThread(r: Runnable?): Thread {
        val t = object : Thread(r, namPrefix + andIncrement) {
            override fun run() {
                try {
                    super.run()
                } catch (t: Throwable) {
                    UtilsBridge.e("Request threw uncaught throwable", t)
                }
            }
        }

        t.isDaemon = isDaemon
        t.setUncaughtExceptionHandler { _, throwable -> println(throwable) }
        t.priority = priority
        return t
    }

    override fun toByte(): Byte = throw UnsupportedOperationException()

    override fun toChar(): Char = throw UnsupportedOperationException()

    override fun toShort(): Short = throw UnsupportedOperationException()
}