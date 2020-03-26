package me.gravitinos.bedwars.gamecore.util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

public class WeakList<T> extends ArrayList<T> {

    private ArrayList<WeakReference<T>> items = new ArrayList<>();

    /**
     * Makes new WeakList
     */
    public WeakList(){}

    /**
     * Create a WeakList from an existing collection
     * @param c Collection
     */
    public WeakList(Collection<T> c) {
        addAll(0, c);
    }

    public boolean add(T element){
        if(this.contains(element)){
            return false;
        }
        return items.add(new WeakReference<>(element));
    }

    @Override
    public void forEach(Consumer<? super T> action){
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        final int size = this.size();
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            action.accept(this.get(i));
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    public void add(int index, T element) {
        if(this.contains(element)){
            return;
        }
        items.add(index, new WeakReference<>(element));
    }

    @NotNull
    @Override
    public WeakListIterator iterator() {
        return new WeakListIterator();
    }

    @Override
    public boolean contains(Object item){
        removeReleased();
        for(WeakReference<T> references : this.items){
            if((item == null ? references.get() == null : item.equals(references.get()))){
                return true;
            }
        }
        return false;
    }

    public int size() {
        removeReleased();
        return items.size();
    }

    public T get(int index) {
        removeReleased();
        return items.get(index).get();
    }

    private void removeReleased() {
        for(int i = 0; i < items.size(); i++){
            WeakReference<T> ref = items.get(i);
            if (ref.get() == null) {
                items.remove(i);
                i--;
            }
        }
    }

    public boolean remove(Object o){
        if(!this.contains(o)){
            return false;
        }

        this.items.removeIf(ref -> o == null ? ref.get() == null : o.equals(ref.get()));

        return true;
    }

    public T remove(int index){
        if(index >= this.size()){
            throw new IndexOutOfBoundsException();
        }
        T t = this.get(index);
        this.items.remove(index);
        return t;
    }

    private class WeakListIterator implements Iterator<T> {

        private int n;
        private int i;

        public WeakListIterator() {
            n = size();
            i = 0;
        }

        public boolean hasNext() {
            return i < n;
        }

        public T next() {
            return get(i++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}