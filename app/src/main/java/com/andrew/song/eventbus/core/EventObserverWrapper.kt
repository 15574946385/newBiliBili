package com.andrew.song.eventbus.core

import androidx.lifecycle.Observer
import com.andrew.song.util.reflect.ReflectHelper

class EventObserverWrapper<T>(
    liveData: EventLiveData<T>,
    sticky: Boolean,
    private val observerDelegate: Observer<in T>
) : Observer<T> {

    private var preventNextEvent = false

    companion object {
        private const val START_VERSION = -1
    }

    init {
        if (!sticky) {
            val version = com.andrew.song.util.reflect.ReflectHelper.of(liveData).getField("mVersion") as? Int ?: START_VERSION
            preventNextEvent = version > START_VERSION
        }
    }

    override fun onChanged(t: T) {
        if (preventNextEvent) {
            preventNextEvent = false
            return
        }
        observerDelegate.onChanged(t)
    }
}