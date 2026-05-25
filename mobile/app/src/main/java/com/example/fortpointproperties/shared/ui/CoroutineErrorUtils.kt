package com.example.fortpointproperties.shared.ui

import java.util.concurrent.CancellationException as JavaCancellationException
import kotlinx.coroutines.CancellationException

fun Throwable.isHarmlessCancellation(): Boolean {
    val text = message.orEmpty()
    return this is CancellationException ||
        this is JavaCancellationException ||
        text.contains("Job was cancelled", ignoreCase = true) ||
        text.contains("StandaloneCoroutine was cancelled", ignoreCase = true)
}
