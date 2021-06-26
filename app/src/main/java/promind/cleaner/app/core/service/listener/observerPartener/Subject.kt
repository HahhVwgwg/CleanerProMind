package promind.cleaner.app.core.service.listener.observerPartener

interface Subject<T, K> {
    fun registerObserver(observer: T)
    fun removeObserver(observer: T)
    fun notifyObservers(notify: K)
}