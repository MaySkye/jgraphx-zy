package com.mxgraph.examples.swing.match;

import java.util.Map;
import java.util.Objects;


public class ResourceStaticData {
    public Map<String, Integer> cellMap; // cell numbers
    public Map<MTriple<String, String, String>, Integer> connMap; // cell connection(sub pred obj) numbers

    public static class MTriple<T, U, V> {

        private T sub;
        private U pred;
        private V obj;

        public MTriple(T t, U u, V v) {
            this.sub = t;
            this.pred = u;
            this.obj = v;
        }

        public T getFirst() {
            return this.sub;
        }

        public U getSecond() {
            return this.pred;
        }

        public V getThird() {
            return this.obj;
        }

        public int hashCode() {
            int x = 99;
            x = x << 1 ^ Objects.hashCode(sub);
            x = x << 1 ^ Objects.hashCode(pred);
            x = x << 1 ^ Objects.hashCode(obj);
            return x;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (!(other instanceof MTriple)) {
                return false;
            } else {
                MTriple x = (MTriple) other;
                if (Objects.equals(this.sub, x.sub) && Objects.equals(this.pred, x.pred)
                        && Objects.equals(this.obj, x.obj)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
