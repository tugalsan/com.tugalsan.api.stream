package com.tugalsan.api.stream.client;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TGS_StreamReverseIterableFromList<T> implements Iterable<T> {

    public TGS_StreamReverseIterableFromList(List<T> l) {
        this.lst = l;
    }
    private final List<T> lst;

    @Override
    public Iterator<T> iterator() {
        return new TGS_StreamReverseIterator(lst.listIterator(lst.size()));
    }

    private static class TGS_StreamReverseIterator<T> implements Iterator {

        public TGS_StreamReverseIterator(ListIterator it) {
            this.it = it;
        }

        private final ListIterator<T> it;

        @Override
        public boolean hasNext() {
            return it.hasPrevious();
        }

        @Override
        public T next() {
            return it.previous();
        }

        @Override
        public void remove() {
            it.remove();
        }
    }

}
