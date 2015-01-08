package com.perry.urlshortener.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
 */
public class BigOrderedRAMSet<E> extends AbstractBigOrderedSet<E> {
    public static final int DEFAULT_PAGE_SIZE = 1024*1024;
    
    private final int PAGE_SIZE;
    private List<Object[]> pages = Collections.synchronizedList(new ArrayList<Object[]>());
    private Map<E,Long> searchIndex = new HashMap<>();
    private long index=0;
    private int pageNo =-1;

    public BigOrderedRAMSet() {
        this(DEFAULT_PAGE_SIZE); // 1 Megabyte
    }

    public BigOrderedRAMSet(int pageSize) {
        this.PAGE_SIZE = pageSize;
    }

    @Override
    public E get(long i) {
        synchronized (this) {
            if (i >= index) {
                throw new NoSuchElementException("[" + i + "] out of bounds [0-" + (index - 1) + "]");
            }
        }
        
        int pageNo = (int) (i / PAGE_SIZE);
        int indexWithinPage = (int) (i % PAGE_SIZE);
        Object[] page = pages.get(pageNo);

        @SuppressWarnings("unchecked")
        E element = (E) page[indexWithinPage];
        return element;
    }
    
    @Override
    public void doClose() {}

    @Override
    public Long find(E element) {
        return searchIndex.get(element);
    }
    
    @Override
    public Appender<E> getAppender() {
        return new RAMSetAppender();
    }
    
    public class RAMSetAppender implements Appender<E> {

        private final long indexForThisThread;
        private final int pageNoForThisThread;
        
        public RAMSetAppender() {
            this.indexForThisThread = index++;
            if (indexForThisThread % PAGE_SIZE == 0) {
                addPage();
            }
            this.pageNoForThisThread=pageNo;
        }

        @Override
        public long getIndex() {
            return indexForThisThread;
        }

        @Override
        public long append(E element) {
            Object[] page = pages.get(pageNoForThisThread);
            int indexWithinPage = (int) (indexForThisThread % PAGE_SIZE);
            page[indexWithinPage] = element;
            searchIndex.put(element, indexForThisThread);

            return indexForThisThread;
        }

        private void addPage() {
            pageNo++;
            pages.add(new Object[PAGE_SIZE]);
        }

    }
}
