package com.moscona.util.collections;

import java.io.Serializable;

/**
 * Created: 3/7/11 1:53 PM
 * By: Arnon Moscona
 * FIXME should be replaced by or inherited from org.apache.commons.lang3.tuple.Pair
 */
public class Pair<A,B> implements Serializable {
    private static final long serialVersionUID = 3624503070552305383L;

    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair)obj;
        return other.first.equals(first) && other.second.equals(second);
    }
}

