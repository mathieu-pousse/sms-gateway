package com.zenika.sms.service;

import android.os.Binder;

/**
 * Useful to retrieve the service instance.
 */
public class LocalServiceBinder<T> extends Binder {
    private T instance;

    public LocalServiceBinder(T instance) {
        this.instance = instance;
    }

    public T getInstance() {
        return this.instance;
    }
}