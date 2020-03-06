package me.gravitinos.bedwars.gamecore.util.Menus;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class WeakListMenu<T> extends ArrayList<T> {

    private ArrayList<WeakReference<T>> items;

    /** Creates new WeakList */
    public WeakListMenu() {
        items = new ArrayList<>();
    }

    public WeakListMenu(Collection c) {
        items = new ArrayList<>();
        addAll(0, c);
    }

    public boolean add(Object element){
        if(this.contains(element)){
            return false;
        }
        return items.add(new WeakReference(element));
    }

    public void add(int index, Object element) {
        if(this.contains(element)){
            return;
        }
        items.add(index, new WeakReference(element));
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new WeakListIterator();
    }

    @Override
    public boolean contains(Object item){
        removeReleased();
        for(WeakReference<T> references : this.items){
            if((references.get() == null ? item == null : item.equals(references.get()))){
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
        return items.get(index).get();
    }

    private void removeReleased() {
        for(int i = 0; i < items.size(); i++){
            WeakReference ref = (WeakReference) items.get(i);
            if (ref.get() == null) {
                items.remove(i);
                i--;
            }
        }
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