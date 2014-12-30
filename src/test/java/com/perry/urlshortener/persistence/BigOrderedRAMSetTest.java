package com.perry.urlshortener.persistence;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class BigOrderedRAMSetTest {

    @Test
    public void shouldReturnExpectedIndex_WhenUniqueEntryAdded() {
        BigOrderedSet<String> set = new BigOrderedRAMSet<>();
        assertThat(set.add("entry1"), equalTo(0L));
        assertThat(set.add("entry2"), equalTo(1L));
    }

    @Test
    public void shouldReturnOriginalIndex_WhenDuplicateEntryAdded() {
        BigOrderedSet<String> set = new BigOrderedRAMSet<>();
        assertThat(set.add("entry1"), equalTo(0L));
        assertThat(set.add("entry1"), equalTo(0L));
    }
    
    @Test
    public void shouldReturnExpectedElement_WhenIndexSupplied() {
        BigOrderedSet<String> set = new BigOrderedRAMSet<>();
        set.add("entry1");
        set.add("entry2");

        assertThat(set.get(0L), equalTo("entry1"));
        assertThat(set.get(1L), equalTo("entry2"));
    }
    
    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void shouldThrowException_WhenRequestingIndexOutOfBounds() {
        BigOrderedSet<String> set = new BigOrderedRAMSet<>();
        set.get(0L);
    }

    @Test
    public void shouldAddElement_WhenFirstPageFull() {
        BigOrderedSet<String> set = new BigOrderedRAMSet<>(2);
        assertThat(set.add("entry1"), equalTo(0L));
        assertThat(set.add("entry2"), equalTo(1L));
        assertThat(set.add("entry3"), equalTo(2L));
        assertThat(set.add("entry4"), equalTo(3L));
        assertThat(set.get(3L), equalTo("entry4"));
    }

    @Test
    public void shouldAddElement_WhenSecondPageFull() {
        BigOrderedSet<String> set = new BigOrderedRAMSet<>(2);
        assertThat(set.add("entry1"), equalTo(0L));
        assertThat(set.add("entry2"), equalTo(1L));
        assertThat(set.add("entry3"), equalTo(2L));
        assertThat(set.add("entry4"), equalTo(3L));
        assertThat(set.add("entry5"), equalTo(4L));
        assertThat(set.add("entry6"), equalTo(5L));

        assertThat(set.get(5L), equalTo("entry6"));
    }

    @Test
    public void shouldNotifyDiskWriter_WhenEntryAdded() {
        final ArrayList<String> entries = new ArrayList<>();
        SetModificationListener<String> listener = new SetModificationListener<String>() {
            
            @Override
            public void add(long index, String s) {
                entries.add((int)index, s);
                
            }
        };

        BigOrderedSet<String> set = new BigOrderedRAMSet<>(2, listener);
        set.add("entry1");
        assertThat(entries.get(0), equalTo("entry1"));
        
    }
}