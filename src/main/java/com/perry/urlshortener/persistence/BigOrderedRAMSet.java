package com.perry.urlshortener.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a set which maintains the order of its entries, 
 * and ensures entries are unique.
 *  
 * The set has a maximum number of entries of 4 Exabytes (4.611686018e+18 bytes)
 * which clearly would not happen since each entry requires 3x8 byte references, 1 8 byte Long value, stringlength so e.g. 64 bytes with a perfectly efficient hashmap.
 * Since you are unlikely to have even 32GB of RAM, it is unlikely you could hold
 * half a billion entries in RAM.
 * 
 * Therefore a database or disk cache would be required to replace this set if
 * more entries are required.
 * 
 * Note that RAM usage is improved by storing UTF-8 strings but this assumes URLs only contain UTF-8 characters.
 *  
 * @param <E>
 */
public class BigOrderedRAMSet<E> implements BigOrderedSet<E> {
    public static final int DEFAULT_PAGE_SIZE = 1024*1024;
    
    private final int PAGE_SIZE;
    private List<Object[]> pages = Collections.synchronizedList(new ArrayList<Object[]>());
    private Map<E,Long> searchIndex = new HashMap<>();
    private long index=0;
    private int pageNo =-1;
    private final SetModificationListener<E> listener;
    
    public BigOrderedRAMSet() {
        this(DEFAULT_PAGE_SIZE); // 1 Megabyte
    }

    public BigOrderedRAMSet(int pageSize) {
        this(pageSize, null);
    }

    public BigOrderedRAMSet(int pageSize, SetModificationListener<E> listener) {
        this.PAGE_SIZE = pageSize;
        this.listener=listener;
    }

    @Override
    public long add(E element) {
        long indexForThisThread;
        int pageNo;
        
        synchronized(this) {
            Long indexOfElement = searchIndex.get(element);
            if (indexOfElement != null) {
                return indexOfElement;
            } else {
                indexForThisThread = index++;
                if (indexForThisThread % PAGE_SIZE == 0) {
                    addPage();
                }
                pageNo=this.pageNo;
            }
            if(listener!=null) {
                listener.add(indexForThisThread, element);
            }
        }
        
        Object[] page = pages.get(pageNo);
        int indexWithinPage = (int) (indexForThisThread % PAGE_SIZE);
        page[indexWithinPage] = element;
        searchIndex.put(element, indexForThisThread);

        return indexForThisThread;
    }

    private void addPage() {
        pageNo++;
        pages.add(new Object[this.PAGE_SIZE]);
    }

    @Override
    public E get(long i) {
        synchronized (this) {
            if (i >= index) {
                throw new ArrayIndexOutOfBoundsException("[" + i + "] out of bounds [0-" + (index - 1) + "]");
            }
        }
        
        int pageNo = (int) (i / PAGE_SIZE);
        int indexWithinPage = (int) (i % PAGE_SIZE);
        Object[] page = pages.get(pageNo);

        @SuppressWarnings("unchecked")
        E element = (E) page[indexWithinPage];
        return element;
    }
}
