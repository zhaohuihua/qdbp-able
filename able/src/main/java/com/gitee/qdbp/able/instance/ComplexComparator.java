package com.gitee.qdbp.able.instance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * Copy form CompoundComparator.<br>
 * A comparator that chains a sequence of one or more Comparators.
 * <p>
 * A compound comparator calls each Comparator in sequence until a single Comparator returns a non-zero result, or the
 * comparators are exhausted and zero is returned.
 * <p>
 * This facilitates in-memory sorting similar to multi-column sorting in SQL. The order of any single Comparator in the
 * list can also be reversed.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 */
public class ComplexComparator<T> implements Comparator<T>, Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    private final List<Comparator<? extends T>> comparators;

    /**
     * Construct a ComplexComparator with initially no Comparators. <br>
     * Clients must add at least one Comparator before calling the compare method or an IllegalStateException is thrown.
     */
    public ComplexComparator() {
        this.comparators = new ArrayList<>();
    }

    /**
     * Construct a ComplexComparator from the Comparators in the provided array.
     * 
     * @param comparators the comparators to build into a compound comparator
     */
    @SafeVarargs
    public ComplexComparator(Comparator<? extends T>... comparators) {
        VerifyTools.requireNotBlank(comparators, "Comparators must not be null");
        this.comparators = new ArrayList<>(comparators.length);
        for (Comparator<? extends T> comparator : comparators) {
            addComparator(comparator);
        }
    }

    /**
     * Add a Comparator to the end of the chain.
     * 
     * @param comparator the Comparator to add to the end of the chain
     */
    public void addComparator(Comparator<? extends T> comparator) {
        this.comparators.add(comparator);
    }

    /**
     * Replace the Comparator at the given index.
     * 
     * @param index the index of the Comparator to replace
     * @param comparator the Comparator to place at the given index
     */
    public void setComparator(int index, Comparator<? extends T> comparator) {
        this.comparators.set(index, comparator);
    }

    /**
     * Returns the number of aggregated comparators.
     * 
     * @return number
     */
    public int getComparatorCount() {
        return this.comparators.size();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int compare(T o1, T o2) {
        if (this.comparators.isEmpty()) {
            throw new IllegalStateException("No sort definitions have been added to this ComplexComparator to compare");
        }
        for (Comparator comparator : this.comparators) {
            int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ComplexComparator)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ComplexComparator<T> other = (ComplexComparator<T>) obj;
        return this.comparators.equals(other.comparators);
    }

    @Override
    public int hashCode() {
        return this.comparators.hashCode();
    }

    @Override
    public String toString() {
        return "ComplexComparator: " + this.comparators;
    }

}
