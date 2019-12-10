package com.mxgraph.examples.swing.decode;

import java.util.Objects;


public class Pair<T, V> {

    private T mt;
    private V mv;

    public Pair() {
    }

    public Pair(T t, V v) {
        mt = t;
        mv = v;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }
        Pair other = (Pair) obj;
        if (other.getFirst().getClass() != this.getFirst().getClass() ||
                other.getSecond().getClass() != this.getSecond().getClass()) {
            return false;
        }
        return Objects.equals(mt, other.mt) && Objects.equals(mv, other.mv);
    }

    @Override
    public int hashCode() {
        return mt.hashCode() ^ mv.hashCode();
    }

    public T getFirst() {
        return mt;
    }

    public V getSecond() {
        return mv;
    }

    public void setFirst(T t) {
        mt = t;
    }

    public void setSecond(V v) {
        mv = v;
    }

    @Override
    public String toString() {
        return "first: " + mt + " second: " + mv;
    }
}
