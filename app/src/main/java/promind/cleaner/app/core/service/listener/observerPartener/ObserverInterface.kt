package promind.cleaner.app.core.service.listener.observerPartener

interface ObserverInterface<T> {
    fun notifyAction(action: T)
}