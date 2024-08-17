package com.mythmc.impl.database;

public interface DatabaseCallback<T> {
    void onQueryDone(T result);
}
