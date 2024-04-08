package org.example.newsfeedapiv2.services;

import java.util.Collection;

public interface CRUDService<T> {
    Collection<T> getAll();
    T getById(Long id);
    T create (T item);
    T update(T item);
    void delete(Long id);
}