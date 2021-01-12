package com.graniteng.hardnessconverter.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * A subclass of SingleLiveEvent that handles sending message events.
 *
 * @see https://medium.com/@erik_90880/sending-events-from-an-mvvm-view-model-with-kotlin-19fdce61dcb9
 * @see https://stackoverflow.com/a/54275216/7381554
 */
class LiveMessageEvent<T> : SingleLiveEvent<(T.() -> Unit)?>() {

    /**
     * Called by the Activity to attach a receiver to the LiveMessageEvent
     *
     * The Activity passes itself as the owner and the object which implements the event interface
     * (it may be the Activity itself) as receiver.
     * The method adds an observer to the LiveData which calls the receiver when it receives an
     * event.
     */
    fun setEventReceiver(owner: LifecycleOwner, receiver: T) {
        observe(owner, Observer { event ->
            if (event != null) {
                receiver.event()
            }
        })
    }

    /**
     * Called by the ViewModel to send an event.
     */
    fun sendEvent(event: (T.() -> Unit)?) {
        value = event
    }
}