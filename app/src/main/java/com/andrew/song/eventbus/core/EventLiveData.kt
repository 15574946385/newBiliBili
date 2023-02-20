package com.andrew.song.eventbus.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class EventLiveData<T> : com.andrew.song.eventbus.core.MutableLiveData<T>() {

    fun observe(owner: LifecycleOwner, sticky: Boolean, observer: Observer<in T>) {
        observe(owner, wrapObserver(sticky, observer))
    }

    private fun wrapObserver(sticky: Boolean, observer: Observer<in T>): Observer<T> {
        return EventObserverWrapper(this, sticky, observer)
    }
}