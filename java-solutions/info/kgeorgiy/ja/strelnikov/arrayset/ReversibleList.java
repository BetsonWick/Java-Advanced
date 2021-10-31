package info.kgeorgiy.ja.strelnikov.arrayset;

import java.util.*;

public class ReversibleList<E> extends AbstractList<E> implements RandomAccess {
    private final List<E> list;
    private final boolean isReversed;

    public ReversibleList(List<E> list, boolean isReversed) {
        this.list = Collections.unmodifiableList(list);
        this.isReversed = isReversed;
    }

    public ReversibleList(ReversibleList<E> toReverse, boolean doReverse) {
        this.list = toReverse.list;
        this.isReversed = toReverse.isReversed ^ doReverse;
    }

    public ReversibleList(TreeSet<E> set) {
        this(new ArrayList<>(set), false);
    }

    @Override
    public ReversibleList<E> subList(int left, int right) {
        int l = index(isReversed ? right - 1 : left);
        int r = isReversed ? index(left) + 1 : index(right);
        return new ReversibleList<>(list.subList(l, r), isReversed);
    }
    
    private int index(int index) {
        return isReversed ? size() - 1 - index : index;
    }

    @Override
    public E get(int i) {
        return list.get(index(i));
    }

    @Override
    public int size() {
        return list.size();
    }

    public boolean isReversed() {
        return isReversed;
    }
}
