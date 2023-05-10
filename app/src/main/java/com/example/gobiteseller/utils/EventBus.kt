package com.example.gobiteseller.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

object EventBus {
     val flow = MutableSharedFlow<Any>(replay = 1)

    fun send(event: Any) {
        GlobalScope.launch(Dispatchers.IO) {
            flow.emit(event)
        }
    }

    inline fun <reified T> asFlow(): Flow<T> {
        return flow.filterIsInstance<T>()
    }
}
