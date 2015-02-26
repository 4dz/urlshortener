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
    private final ArrayList<LazyFixedSizeArray> __unsynchronizedList = new ArrayList<>();
    private final List<LazyFixedSizeArray> pages = Collections.synchronizedList(__unsynchronizedList);
    private final Map<E,Long> searchIndex = new HashMap<>();
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
        
        int pageNo = (int) (i / PAGE_SIZE);
        synchronized (this) {
            if (pageNo >= pages.size()) {
                throw new NoSuchElementException("[" + i + "] out of bounds.");
            }
        }
        
        
        int indexWithinPage = (int) (i % PAGE_SIZE);
        LazyFixedSizeArray page = pages.get(pageNo);

        @SuppressWarnings("unchecked")
        E element = (E) page.get(indexWithinPage);
        
        if(element==null) {
            throw new NoSuchElementException("[" + i + "] does not exist.");
        }
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

    @Override
    public void mirror(Long id, E value) {
        int pageNo = (int)(id/PAGE_SIZE);
        if(pageNo>=pages.size()) {
            synchronized(pages) {
                __unsynchronizedList.ensureCapacity(pageNo+1);
                for(int i=pageNo-pages.size()+1; i>0; i--) {
                    pages.add(new LazyFixedSizeArray(PAGE_SIZE));
                }
            }
        }
        
        int offset = (int)(id%PAGE_SIZE);
        LazyFixedSizeArray page = pages.get(pageNo);
        page.set(offset,value);
        searchIndex.put(value, id);
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
            LazyFixedSizeArray page = pages.get(pageNoForThisThread);
            int indexWithinPage = (int) (indexForThisThread % PAGE_SIZE);
            page.set(indexWithinPage,element);
            searchIndex.put(element, indexForThisThread);

            return indexForThisThread;
        }

        private void addPage() {
            pageNo++; 
            if(pages.size() <= pageNo) {
                pages.add(new LazyFixedSizeArray(PAGE_SIZE));
            }
        }

    }
}
