package io.copymaker.auctionsniper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class Announcer<T extends EventListener> {

    private final T proxy;
    private final List<T> listeners = new ArrayList<>();

    public Announcer(Class<? extends T> listenerType) {
        this.proxy = listenerType.cast(Proxy.newProxyInstance(
                listenerType.getClassLoader(),
                new Class<?>[]{listenerType},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        announce(method, args);
                        return null;
                    }
                }
        ));
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
    }

    public static <T extends EventListener> Announcer<T> to(Class<? extends T> listenerType) {
        return new Announcer<T>(listenerType);
    }

    public T announce() {
        return proxy;
    }

    private void announce(Method method, Object[] args) {
        try {
            for (T listener : listeners) {
                method.invoke(listener, args);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not invoke listener", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new UnsupportedOperationException("Listener threw exception", cause);
            }
        }
    }
}
