package me.redplayer_1.custombosses.util;

import me.redplayer_1.custombosses.CustomBosses;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Creates a list that removes elements after a delay. Each element has its own {@link org.bukkit.scheduler.BukkitRunnable},
 * potentially causing server lag with larger lists.
 *
 * @param <T>
 */
public class CachedList<T> implements Iterable<T> {
    private record CacheEntry<E>(E item, long insertTime) {
    }

    private final LinkedList<CacheEntry<T>> list = new LinkedList<>();
    private Consumer<T> removeAction = null;
    private final int lifetime;
    private final TimeUnit unit;

    /**
     * @param lifetime the lifetime of elements added to the list
     * @param unit     the unit of time the lifetime represents
     */
    public CachedList(int lifetime, TimeUnit unit) {
        this.lifetime = lifetime;
        this.unit = unit;
    }

    /**
     * @param lifetime the lifetime of elements added to the list
     * @param unit     the unit of time the lifetime represents
     * @param contents initial contents to load into the list
     */
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
    public CachedList(int lifetime, TimeUnit unit, @Nullable Consumer<T> removeAction) {
        this(lifetime, unit);
        this.removeAction = removeAction;
    }

    /**
     * Adds the element to the list
     */
    public void add(T element) {
        list.add(new CacheEntry<>(element, System.currentTimeMillis()));
        Bukkit.getAsyncScheduler().runDelayed(CustomBosses.getInstance(), task -> {
            remove(element);
            if (removeAction != null) removeAction.accept(element);
        }, lifetime, unit);
    }

    /**
     * Removes the element at that index from the list
     *
     * @return the element that was removed
     * @throws IndexOutOfBoundsException if the index was invalid
     */
    public T remove(int index) throws IndexOutOfBoundsException {
        return list.remove(index).item;
    }

    /**
     * Removes the element from the list
     *
     * @return the element that was removed or null if it didn't exist
     */
    public @Nullable T remove(T element) {
        try {
            return remove(indexOf(element));
        } catch (IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    /**
     * @return the element at that index
     * @throws IndexOutOfBoundsException if the given index is invalid
     */
    public T get(int index) throws IndexOutOfBoundsException {
        return list.get(index).item;
    }

    /**
     * @param element the element to search for
     * @return the index of the element in the list, or -1 if it isn't found
     */
    public int indexOf(T element) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).item == element) return i;
        }
        return -1;
    }

    /**
     * @param element the element to search for
     * @return if the list contains the element
     */
    public boolean contains(T element) {
        return indexOf(element) > -1;
    }

    /**
     * Clears all contents from the list
     */
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
}
