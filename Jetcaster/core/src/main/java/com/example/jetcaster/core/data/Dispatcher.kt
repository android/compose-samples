package com.example.jetcaster.core.data

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val jetcasterDispatcher: JetcasterDispatchers)

enum class JetcasterDispatchers {
    Main,
    IO,
}
