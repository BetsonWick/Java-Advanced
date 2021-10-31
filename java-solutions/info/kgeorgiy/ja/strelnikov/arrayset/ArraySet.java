package info.kgeorgiy.ja.strelnikov.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private final ReversibleList<E> list;
    private final Comparator<? super E> comparator;

    private ArraySet(ReversibleList<E> list, Comparator<? super E> comparator) {
        this.list = list;
        this.comparator = comparator;
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Comparator<? super E> comparator) {
        this(Collections.emptyList(), comparator);
    }

    public ArraySet(Collection<? extends E> list) {
        this(list, null);
    }

    public ArraySet(Collection<? extends E> list, Comparator<? super E> comparator) {
        TreeSet<E> toAdd = new TreeSet<>(comparator);
        toAdd.addAll(list);
        this.list = new ReversibleList<>(toAdd);
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    private int compare(E first, E second) {
        return comparator == null ? ((Comparable<E>) first).compareTo(second) : comparator.compare(first, second);
    }

    private E getElement(int index) {
        E element = getElementOrNull(index);
        if (element == null) {
            throw new NoSuchElementException("There is no element on that index!");
        }
        return element;
    }

    private boolean isIndexInBounds(int index) {
        return index >= 0 && index < size();
    }

    private E getElementOrNull(int index) {
        return isIndexInBounds(index) ? list.get(index) : null;
    }

    private int binarySearch(E element) {
        return Collections.binarySearch(list, Objects.requireNonNull(element), comparator);
    }

    private int indexOf(E element, boolean includeFound, boolean beforeInsertionPoint) {
        int foundIndex = binarySearch(element);
        if (foundIndex < 0) {
            return -foundIndex - (beforeInsertionPoint ? 2 : 1);
        }
        if (includeFound) {
            return foundIndex;
        }
        return beforeInsertionPoint ? (foundIndex - 1) : (foundIndex + 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return binarySearch((E) o) >= 0;
    }

    @Override
    public E lower(E e) {
        return getElementOrNull(indexOf(e, false, true));
    }

    @Override
    public E floor(E e) {
        return getElementOrNull(indexOf(e, true, true));
    }

    @Override
    public E ceiling(E e) {
        return getElementOrNull(indexOf(e, true, false));
    }

    @Override
    public E higher(E e) {
        return getElementOrNull(indexOf(e, false, false));
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("This ArraySet is unmodifiable!");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("This ArraySet is unmodifiable!");
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(new ReversibleList<>(list, true), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    private NavigableSet<E> getEmptyCopy() {
        return new ArraySet<>(comparator);
    }

    private NavigableSet<E> getSubSet(E left, boolean includesLeft, E right, boolean includesRight) {
        int leftIndex = indexOf(left, includesLeft, false);
        int rightIndex = indexOf(right, includesRight, true);
        if (leftIndex <= rightIndex) {
            return new ArraySet<>(list.subList(leftIndex, rightIndex + 1), comparator);
        }
        return getEmptyCopy();
    }

    @Override
    public NavigableSet<E> subSet(E left, boolean b, E right, boolean b1) {
        if (compare(left, right) > 0) {
            throw new IllegalArgumentException("Invalid borders of an interval!");
        }
        return getSubSet(left, b, right, b1);
    }

    @Override
    public NavigableSet<E> headSet(E e, boolean b) {
        if (isEmpty()) {
            return this;
        }
        return getSubSet(first(), true, e, b);
    }

    @Override
    public NavigableSet<E> tailSet(E e, boolean b) {
        if (isEmpty()) {
            return this;
        }
        return getSubSet(e, b, last(), true);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E e, E e1) {
        return subSet(e, true, e1, false);
    }

    @Override
    public SortedSet<E> headSet(E e) {
        return headSet(e, false);
    }

    @Override
    public SortedSet<E> tailSet(E e) {
        return tailSet(e, true);
    }


    @Override
    public E first() {
        return getElement(0);
    }

    @Override
    public E last() {
        return getElement(size() - 1);
    }

    @Override
    public int size() {
        return list.size();
    }
}
