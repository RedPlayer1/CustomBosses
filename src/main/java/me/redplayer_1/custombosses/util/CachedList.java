package me.redplayer_1.custombosses.util;

import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class CachedList<T> implements Iterable<T>{
    private final HashMap<T, Long> list = new HashMap<>();

    public CachedList(int lifetime, TimeUnit unit) {
        Bukkit.getAsyncScheduler().runAtFixedRate(CustomBosses.getInstance(), task -> {
            list.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue() >= unit.toMillis(lifetime));
        }, lifetime, lifetime, unit);
    }

    @SafeVarargs
    public CachedList(int lifetime, TimeUnit unit, T... contents) {
        this(lifetime, unit);
        for (T item : contents) {
            add(item);
        }
    }

    public void add(T e) {
        list.add(new CacheEntry<>(e, System.currentTimeMillis()));
    }

    public T remove(int index) {
        return list.remove(index).item;
    }

    public T get(int index) {
        return list.get(index).item;
    }

    public boolean contains(T e) {
        return list.containsKey(e);
    }

    public void clear() {
        list.clear();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int index = -1;

            @Override
            public boolean hasNext() {
                return index >= list.size();
            }

            @Override
            public T next() {
                index++;
                return get(index);
            }
        };
    }

    private record CacheEntry<E>(E item, long insertTime) {}
}
