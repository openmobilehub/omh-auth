package com.omh.android.auth.api

class CancellableCollector {

    private val cancellables : MutableCollection<OmhCancellable> = mutableSetOf()

    fun addCancellable(cancellable: OmhCancellable?) {
        if (cancellable == null) return
        cancellables.add(cancellable)
    }

    fun clear() {
        cancellables.forEach(OmhCancellable::cancel)
        cancellables.clear()
    }
}
