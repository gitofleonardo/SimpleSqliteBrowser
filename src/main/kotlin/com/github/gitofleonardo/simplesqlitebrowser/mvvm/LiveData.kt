package com.github.gitofleonardo.simplesqlitebrowser.mvvm

class LiveData<T> {
    private val observers: MutableList<LiveDataObserver<T>> = ArrayList()
    private var data: T? = null

    var value: T?
        get() = data
        set(value) {
            data = value
            data?.let { notifyChange(it) }
        }

    private fun notifyChange(data: T) {
        for (observer in observers) {
            observer.onChanged(data)
        }
    }

    fun observe(observer: LiveDataObserver<T>) {
        observers.add(observer)
    }

    fun observe(observer: (T) -> Unit) {
        observers.add(object : LiveDataObserver<T> {
            override fun onChanged(newData: T) {
                observer.invoke(newData)
            }
        })
    }

    @FunctionalInterface
    interface LiveDataObserver<T> {
        fun onChanged(newData: T)
    }
}