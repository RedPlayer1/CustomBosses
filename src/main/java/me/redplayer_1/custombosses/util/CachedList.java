package me.redplayer_1.custombosses.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CachedList<T> implements Iterable<T>{
    private record CacheEntry<E>(E item, long insertTime) {}

    private final LinkedList<CacheEntry<T>> list = new LinkedList<>();
    private final ScheduledTask clearTask;
    private Consumer<T> removeAction = null;

    /**
     * @param lifetime the lifetime of elements added to the list
     * @param unit the unit of time the lifetime represents
     */
    public CachedList(int lifetime, TimeUnit unit) {
        clearTask = Bukkit.getAsyncScheduler().runAtFixedRate(CustomBosses.getInstance(),
                task -> {
                    for (int i = 0; i < list.size(); i++) {
                        if (System.currentTimeMillis() - list.get(i).insertTime >= unit.toMillis(lifetime)) {
                            if (removeAction != null) removeAction.accept(list.get(i).item);
                            list.remove(i);
                            i--;
                        }
                    }
                },
                lifetime, lifetime, unit
        );
    }

    @SafeVarargs
    public CachedList(int lifetime, TimeUnit unit, T... contents) {
        this(lifetime, unit);
        for (T item : contents) {
            add(item);
        }
    }

    /**
     * @param removeAction called before an element is removed from the list
     */
    public CachedList(int lifetime, TimeUnit unit, Consumer<T> removeAction) {
        this(lifetime, unit);
        this.removeAction = removeAction;
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
        for (CacheEntry<T> entry : list) {
            if (entry.item == e) return true;
        }
        return false;
    }

    public void clear() {
        list.clear();
    }

    /**
     * Kills the caching task and clears the list. Call this after the list is used for the last time.
     */
    public void destroy() {
        clear();
        clearTask.cancel();
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
}
