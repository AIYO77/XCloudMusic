package com.xw.lib_common.autodispose.lifecycle


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: [autoDisposeInterceptor]
 */

/**
 * Create a [ContinuationInterceptor] that follows lifecycle of LifecycleOwner.
 */
@Suppress("FunctionName")
fun LifecycleOwner.autoDisposeInterceptor(): ContinuationInterceptor =
    LifecycleAutoDisposeInterceptor(this)

/**
 * Create a [ContinuationInterceptor] that follows lifecycle of LifecycleOwner.
 */
@Suppress("FunctionName")
fun LifecycleAutoDisposeInterceptor(lifecycleOwner: LifecycleOwner): ContinuationInterceptor =
    LifecycleAutoDisposeInterceptor(lifecycleOwner.lifecycle)

/**
 * Create a [ContinuationInterceptor] that follows lifecycle.
 */
@Suppress("FunctionName")
fun Lifecycle.autoDisposeInterceptor(): ContinuationInterceptor =
    LifecycleAutoDisposeInterceptor(this)

/**
 * Create a [ContinuationInterceptor] that follows lifecycle.
 */
@Suppress("FunctionName")
fun LifecycleAutoDisposeInterceptor(lifecycle: Lifecycle): ContinuationInterceptor =
    LifecycleAutoDisposeInterceptorImpl(lifecycle)

private class LifecycleAutoDisposeInterceptorImpl(
    private val lifecycle: Lifecycle
) : ContinuationInterceptor {
    override val key: CoroutineContext.Key<*>
        get() = ContinuationInterceptor

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        val job = continuation.context[Job]
        if (job != null) {
            lifecycle.autoDispose(job)
        }
        return continuation
    }
}