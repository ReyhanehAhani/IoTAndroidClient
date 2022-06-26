package com.reyhaneh.ahani.iotclient

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class OptionalNavTarget(
    val navTarget: Navigator.NavTarget,
    val pop: String? = null,
    val inclusive: Boolean = false
)

class Navigator {

    private val _sharedFlow =
        MutableSharedFlow<OptionalNavTarget>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigateTo(navTarget: NavTarget, pop: String? = null, inclusive: Boolean = false) {
        _sharedFlow.tryEmit(OptionalNavTarget(navTarget, pop, inclusive))
    }

    enum class NavTarget(val label: String) {
        Login("login"),
        Summary("summary"),
        Register("register")
    }

    companion object {
        var navigator: Navigator? = null
        fun getInstance() : Navigator {
            if (navigator == null) {
                navigator = Navigator()
            }
            return navigator!!
        }
    }
}