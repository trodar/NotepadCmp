package com.trodar.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class Navigator(
    val state: NavState,
    val topLevelBackEnabled: Boolean
) {
    private val _tabReselections = MutableSharedFlow<TopLevelRoute>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    fun goBack() {
        val currentBackstack = state.currentBackstack

        if (state.topLevelRoute == null) {
            if (currentBackstack.size > 1) {
                currentBackstack.removeLastOrNull()
            }
            return
        }

        if (currentBackstack.size == 1 && state.topLevelRoute != state.primaryTopLevelRoute) {
            if (topLevelBackEnabled) {
                state.topLevelRoute = state.primaryTopLevelRoute
            }
        } else if (currentBackstack.size > 1) {
            currentBackstack.removeLastOrNull()
        }
    }

    fun add(route: AppRoute) {
        if (route is TopLevelRoute) {
            activate(route)
        } else {
            state.currentBackstack.add(route)
        }
    }

    fun set(route: AppRoute) {
        state.currentBackstack.clear()
        add(route)
    }

    fun activate(route: TopLevelRoute, withReselection: Boolean = true) {
        if (withReselection && route == state.topLevelRoute) {
            val currentBackstack = state.currentBackstack

            if (currentBackstack.size > 1) {
                currentBackstack.removeRange(1, currentBackstack.size)
            } else {
                _tabReselections.tryEmit(route)
            }
            return
        }
        state.topLevelRoute = route
    }
}
