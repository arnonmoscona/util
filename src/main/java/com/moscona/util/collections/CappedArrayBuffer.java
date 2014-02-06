package com.moscona.util.collections;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created: Aug 3, 2010 12:40:36 PM
 * By: Arnon Moscona
 * A simple, not super-efficient limited array buffer that caps at a specific capacity
 */
public class CappedArrayBuffer<T> extends ArrayList<T> {
    private static final long serialVersionUID = 1334376019533747683L;
    private int capacity;

    public CappedArrayBuffer(int capacity)  {
        super();
        this.capacity = capacity;
        if (capacity<=0) {
            this.capacity = 1;
        }
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param element element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
     */
    @Override
    public synchronized boolean add(T element) {
        boolean retval = super.add(element);
        enforceCapacity();
        return retval;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public synchronized void add(int index, T element) {
        super.add(index, element);
        enforceCapacity();
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public synchronized boolean addAll(Collection<? extends T> c) {
        boolean retval = super.addAll(c);
        enforceCapacity();
        return retval;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c     collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException      if the specified collection is null
     */
    @Override
    public synchronized boolean addAll(int index, Collection<? extends T> c) {
        boolean retval =  super.addAll(index, c);
        enforceCapacity();
        return retval;
    }

    public synchronized boolean isAtCapacity() {
        return size()>=capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    private synchronized void enforceCapacity() {
        if (size()>capacity) {
            int fromIndex = 0;
            int toIndex = size()-capacity;
            removeRange(fromIndex,toIndex);
        }
    }
}
